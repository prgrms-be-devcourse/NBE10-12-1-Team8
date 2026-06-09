package com.backend.domain.order.order.service;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminOrderService adminOrderService;

    // ─── 헬퍼 ──────────────────────────────────────────────────────────────────

    private Order orderWithStatus(String email, OrderStatus status, LocalDateTime shippingDate) {
        Order order = new Order(email, shippingDate, "서울 강남구 테헤란로 1", "06234", LocalDateTime.now());
        order.updateStatus(status);
        return order;
    }

    // ─── updateShipped() ───────────────────────────────────────────────────────

    @Test
    @DisplayName("단건 주문 SHIPPED 상태로 변경")
    void updateShipped_changesStatusToShipped() {
        Order order = orderWithStatus("a@test.com", OrderStatus.ORDERED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = adminOrderService.updateShipped(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("존재하지 않는 주문 SHIPPED 처리 시 예외")
    void updateShipped_throwsException_whenNotFound() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminOrderService.updateShipped(999L))
                .isInstanceOf(Exception.class);
    }

    // ─── updateStatus() ────────────────────────────────────────────────────────

    @Test
    @DisplayName("주문 상태를 CONFIRMED로 변경")
    void updateStatus_toConfirmed() {
        Order order = orderWithStatus("a@test.com", OrderStatus.ORDERED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = adminOrderService.updateStatus(1L, "CONFIRMED");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("주문 상태를 PREPARING_SHIPMENT로 변경")
    void updateStatus_toPreparingShipment() {
        Order order = orderWithStatus("a@test.com", OrderStatus.CONFIRMED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = adminOrderService.updateStatus(1L, "PREPARING_SHIPMENT");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PREPARING_SHIPMENT);
    }

    @Test
    @DisplayName("주문 상태를 DELIVERED로 변경")
    void updateStatus_toDelivered() {
        Order order = orderWithStatus("a@test.com", OrderStatus.SHIPPED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = adminOrderService.updateStatus(1L, "DELIVERED");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("잘못된 상태 문자열 전달 시 예외")
    void updateStatus_throwsException_whenInvalidStatus() {
        Order order = orderWithStatus("a@test.com", OrderStatus.ORDERED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> adminOrderService.updateStatus(1L, "INVALID_STATUS"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── updateBulkShipped() ───────────────────────────────────────────────────

    @Test
    @DisplayName("여러 주문 일괄 SHIPPED 처리")
    void updateBulkShipped_changesAllToShipped() {
        Order order1 = orderWithStatus("a@test.com", OrderStatus.ORDERED, LocalDateTime.now());
        Order order2 = orderWithStatus("b@test.com", OrderStatus.ORDERED, LocalDateTime.now());
        given(orderRepository.findById(1L)).willReturn(Optional.of(order1));
        given(orderRepository.findById(2L)).willReturn(Optional.of(order2));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        List<Order> results = adminOrderService.updateBulkShipped(List.of(1L, 2L));

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(o -> o.getStatus() == OrderStatus.SHIPPED);
        verify(orderRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("빈 목록 일괄 처리 시 빈 결과 반환")
    void updateBulkShipped_returnsEmpty_whenEmptyList() {
        List<Order> results = adminOrderService.updateBulkShipped(List.of());

        assertThat(results).isEmpty();
        verify(orderRepository, times(0)).save(any());
    }

    // ─── findTodayOrders() ─────────────────────────────────────────────────────

    @Test
    @DisplayName("CANCELED 주문만 오늘 처리 목록에서 제외, DELIVERED는 포함")
    void findTodayOrders_excludesOnlyCanceled() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Order ordered    = orderWithStatus("a@test.com", OrderStatus.ORDERED,    today);
        Order confirmed  = orderWithStatus("b@test.com", OrderStatus.CONFIRMED,  today);
        Order shipped    = orderWithStatus("c@test.com", OrderStatus.SHIPPED,    today);
        Order delivered  = orderWithStatus("d@test.com", OrderStatus.DELIVERED,  today);
        Order canceled   = orderWithStatus("e@test.com", OrderStatus.CANCELED,   today);

        given(orderRepository.findAll()).willReturn(List.of(ordered, confirmed, shipped, delivered, canceled));

        List<Order> result = adminOrderService.findTodayOrders();

        assertThat(result).hasSize(4);
        assertThat(result).noneMatch(o -> o.getStatus() == OrderStatus.CANCELED);
        assertThat(result).anyMatch(o -> o.getStatus() == OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배송 예정일이 오늘 이후(내일)인 주문은 오늘 처리 목록에서 제외")
    void findTodayOrders_excludesFutureShippingDate() {
        LocalDateTime today    = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        Order todayOrder    = orderWithStatus("today@test.com",    OrderStatus.ORDERED, today);
        Order tomorrowOrder = orderWithStatus("tomorrow@test.com", OrderStatus.ORDERED, tomorrow);

        given(orderRepository.findAll()).willReturn(List.of(todayOrder, tomorrowOrder));

        List<Order> result = adminOrderService.findTodayOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("today@test.com");
    }

    @Test
    @DisplayName("배송 예정일이 오늘 또는 과거인 주문은 오늘 처리 목록에 포함")
    void findTodayOrders_includesPastAndTodayShippingDate() {
        LocalDateTime today     = LocalDate.now().atStartOfDay();
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay();
        Order todayOrder     = orderWithStatus("today@test.com",     OrderStatus.ORDERED, today);
        Order yesterdayOrder = orderWithStatus("yesterday@test.com", OrderStatus.ORDERED, yesterday);

        given(orderRepository.findAll()).willReturn(List.of(todayOrder, yesterdayOrder));

        List<Order> result = adminOrderService.findTodayOrders();

        assertThat(result).hasSize(2);
    }
}
