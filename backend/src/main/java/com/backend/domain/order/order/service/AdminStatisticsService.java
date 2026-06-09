package com.backend.domain.order.order.service;

import com.backend.domain.order.order.dto.AdminStatisticsResponse;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.order.orderItem.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public AdminStatisticsResponse getStatistics() {
        // 취소된 주문을 제외한 유효 주문만 통계 계산에 사용
        List<Order> validOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELED)
                .toList();

        // 관리자 통계 화면에 필요한 월별 매출, 최근 일별 매출, 상품별 판매량을 한 번에 반환
        return new AdminStatisticsResponse(
                getMonthlySales(validOrders),
                getRecentDailySales(validOrders),
                getProductSales(validOrders)
        );
    }

    /**
     * 올해 1월부터 12월까지의 월별 매출 통계
     * 매출이 없는 달도 0원으로 표시되도록 1월~12월을 먼저 초기화한 뒤,
     * 올해 주문 금액을 월별로 합산한다.
     */
    private List<AdminStatisticsResponse.MonthlySalesResponse> getMonthlySales(List<Order> orders) {
        int currentYear = LocalDate.now().getYear();
        Map<YearMonth, Integer> salesByMonth = new LinkedHashMap<>();

        IntStream.rangeClosed(1, 12)
                .mapToObj(month -> YearMonth.of(currentYear, month))
                .forEach(month -> salesByMonth.put(month, 0));

        orders.stream()
                .filter(order -> order.getOrderAt().getYear() == currentYear)
                .forEach(order -> {
                    YearMonth month = YearMonth.from(order.getOrderAt());
                    salesByMonth.merge(month, order.calculateTotalPrice(), Integer::sum);
                });

        return salesByMonth.entrySet().stream()
                .map(entry -> new AdminStatisticsResponse.MonthlySalesResponse(
                        entry.getKey().toString(),
                        entry.getValue()
                ))
                .toList();
    }

    /**
     * 오늘을 포함한 최근 7일간의 일별 매출 통계
     *
     * 최근 7일 날짜를 먼저 0원으로 초기화한 뒤,
     * 해당 기간에 발생한 주문 금액을 날짜별로 합산한다.
     */
    private List<AdminStatisticsResponse.DailySalesResponse> getRecentDailySales(List<Order> orders) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        Map<LocalDate, Integer> salesByDate = new LinkedHashMap<>();

        IntStream.rangeClosed(0, 6)
                .mapToObj(startDate::plusDays)
                .forEach(date -> salesByDate.put(date, 0));

        orders.stream()
                .filter(order -> !order.getOrderAt().toLocalDate().isBefore(startDate))
                .filter(order -> !order.getOrderAt().toLocalDate().isAfter(today))
                .forEach(order -> {
                    LocalDate date = order.getOrderAt().toLocalDate();
                    salesByDate.merge(date, order.calculateTotalPrice(), Integer::sum);
                });

        return salesByDate.entrySet().stream()
                .map(entry -> new AdminStatisticsResponse.DailySalesResponse(
                        entry.getKey().toString(),
                        entry.getValue()
                ))
                .toList();
    }

    /**
     * 상품별 누적 판매량 통계
     *
     * 모든 주문의 주문 항목을 기준으로 상품별 판매 수량을 합산하고,
     * 판매량이 많은 상품 순서대로 정렬한다.
     */
    private List<AdminStatisticsResponse.ProductSalesResponse> getProductSales(List<Order> orders) {
        Map<Long, ProductSalesSummary> salesByProduct = new LinkedHashMap<>();

        orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .forEach(orderItem -> {
                    Long productId = orderItem.getProduct().getId();
                    ProductSalesSummary current = salesByProduct.getOrDefault(
                            productId,
                            ProductSalesSummary.from(orderItem)
                    );
                    salesByProduct.put(productId, current.increase(orderItem.getQuantity()));
                });

        return salesByProduct.values().stream()
                .sorted(Comparator.comparingInt(ProductSalesSummary::quantity).reversed())
                .map(summary -> new AdminStatisticsResponse.ProductSalesResponse(
                        summary.productId(),
                        summary.productName(),
                        summary.quantity()
                ))
                .toList();
    }

    /**
     * 상품별 판매량 집계를 위한 내부 요약 객체
     *
     * 상품 ID, 상품명, 누적 판매 수량을 저장한다.
     */
    private record ProductSalesSummary(
            Long productId,
            String productName,
            int quantity
    ) {
        static ProductSalesSummary from(OrderItem orderItem) {
            return new ProductSalesSummary(
                    orderItem.getProduct().getId(),
                    orderItem.getProduct().getName(),
                    0
            );
        }

        ProductSalesSummary increase(int quantity) {
            return new ProductSalesSummary(productId, productName, this.quantity + quantity);
        }
    }
}