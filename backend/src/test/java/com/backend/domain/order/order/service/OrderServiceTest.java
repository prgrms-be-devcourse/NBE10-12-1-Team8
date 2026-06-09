package com.backend.domain.order.order.service;

import com.backend.domain.order.order.dto.OrderItemRequest;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private OrderService orderService;

    // ─── 헬퍼 ──────────────────────────────────────────────────────────────────

    private void fixClock(LocalDateTime dateTime) {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        given(clock.getZone()).willReturn(zone);
        given(clock.instant()).willReturn(dateTime.atZone(zone).toInstant());
    }

    private Product sellingProduct(Long id, String name, int price) {
        Product p = new Product(name, price, "desc", "img");
        // Reflection으로 id 주입 (BaseEntity의 id 필드)
        try {
            var field = p.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(p, id);
        } catch (Exception ignored) {}
        return p;
    }

    private Order orderedOrder(String email) {
        return new Order(email,
                LocalDate.now().atStartOfDay(),
                "서울 강남구 테헤란로 1",
                "06234",
                LocalDateTime.now().minusHours(1));
    }

    // ─── create() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("기존 주문 없으면 새 주문 생성")
    void create_newOrder_whenNoExistingOrder() {
        fixClock(LocalDateTime.of(2026, 6, 9, 10, 0));
        Product product = sellingProduct(1L, "에티오피아", 32000);

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), eq(OrderStatus.CANCELED)))
                .willReturn(Optional.empty());
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create("a@test.com", "서울 강남구", "06234",
                List.of(new OrderItemRequest(1L, 2)));

        assertThat(result.getEmail()).isEqualTo("a@test.com");
        assertThat(result.getOrderItems()).hasSize(1);
        assertThat(result.getOrderItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("동일 배송 조건의 ORDERED 주문이 있으면 상품 합산")
    void create_mergesItems_whenOrderedOrderExists() {
        fixClock(LocalDateTime.of(2026, 6, 9, 10, 0));
        Product product = sellingProduct(1L, "에티오피아", 32000);
        Order existing = orderedOrder("merge@test.com");

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), eq(OrderStatus.CANCELED)))
                .willReturn(Optional.of(existing));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = orderService.create("merge@test.com", "서울 강남구 테헤란로 1", "06234",
                List.of(new OrderItemRequest(1L, 3)));

        assertThat(result).isSameAs(existing);
        assertThat(result.getOrderItems()).hasSize(1);
        assertThat(result.getOrderItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("동일 배송 조건의 주문이 ORDERED가 아니면 예외")
    void create_throwsException_whenExistingOrderNotOrdered() {
        fixClock(LocalDateTime.of(2026, 6, 9, 10, 0));
        Order confirmed = orderedOrder("conf@test.com");
        confirmed.updateStatus(OrderStatus.CONFIRMED);

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), eq(OrderStatus.CANCELED)))
                .willReturn(Optional.of(confirmed));

        assertThatThrownBy(() ->
                orderService.create("conf@test.com", "서울 강남구 테헤란로 1", "06234",
                        List.of(new OrderItemRequest(1L, 1))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 처리 중인 주문이 있어 합산할 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문 시 예외")
    void create_throwsException_whenProductNotFound() {
        fixClock(LocalDateTime.of(2026, 6, 9, 10, 0));

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), any()))
                .willReturn(Optional.empty());
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.create("a@test.com", "서울", "00000",
                        List.of(new OrderItemRequest(999L, 1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("판매중지 상품 주문 시 예외")
    void create_throwsException_whenProductNotSelling() {
        fixClock(LocalDateTime.of(2026, 6, 9, 10, 0));
        Product stopped = sellingProduct(2L, "단종원두", 10000);
        stopped.updateSelling(false);

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), any()))
                .willReturn(Optional.empty());
        given(productRepository.findById(2L)).willReturn(Optional.of(stopped));

        assertThatThrownBy(() ->
                orderService.create("a@test.com", "서울", "00000",
                        List.of(new OrderItemRequest(2L, 1))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("판매중지된 상품은 주문할 수 없습니다.");
    }

    @Test
    @DisplayName("14시 이전 주문이면 배송 예정일은 당일 자정")
    void create_shippingDate_today_whenBefore14() {
        LocalDateTime orderTime = LocalDateTime.of(2026, 6, 9, 13, 59, 59);
        fixClock(orderTime);
        Product product = sellingProduct(1L, "원두", 10000);

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), any()))
                .willReturn(Optional.empty());
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        given(orderRepository.save(captor.capture())).willAnswer(inv -> inv.getArgument(0));

        orderService.create("a@test.com", "서울", "00000", List.of(new OrderItemRequest(1L, 1)));

        assertThat(captor.getValue().getShippingDate())
                .isEqualTo(LocalDate.of(2026, 6, 9).atStartOfDay());
    }

    @Test
    @DisplayName("14시 정각부터는 배송 예정일이 다음날 자정")
    void create_shippingDate_tomorrow_whenFrom14() {
        LocalDateTime orderTime = LocalDateTime.of(2026, 6, 9, 14, 0, 0);
        fixClock(orderTime);
        Product product = sellingProduct(1L, "원두", 10000);

        given(orderRepository.findExistingOrder(any(), any(), any(), any(), any()))
                .willReturn(Optional.empty());
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        given(orderRepository.save(captor.capture())).willAnswer(inv -> inv.getArgument(0));

        orderService.create("a@test.com", "서울", "00000", List.of(new OrderItemRequest(1L, 1)));

        assertThat(captor.getValue().getShippingDate())
                .isEqualTo(LocalDate.of(2026, 6, 10).atStartOfDay());
    }

    // ─── cancel() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ORDERED 주문 취소 → CANCELED 상태로 변경 (삭제 X)")
    void cancel_success_changesStatusToCanceled() {
        Order order = orderedOrder("cancel@test.com");
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        orderService.cancel(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 주문 취소 시 예외")
    void cancel_throwsException_whenOrderNotFound() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancel(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("ORDERED가 아닌 주문 취소 시 예외")
    void cancel_throwsException_whenNotOrdered() {
        Order shipped = orderedOrder("shipped@test.com");
        shipped.updateStatus(OrderStatus.SHIPPED);
        given(orderRepository.findById(1L)).willReturn(Optional.of(shipped));

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문완료 상태의 주문만 취소할 수 있습니다.");
    }

    // ─── modify() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ORDERED 주문 배송지 수정 성공")
    void modify_success() {
        Order order = orderedOrder("mod@test.com");
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Order result = orderService.modify(1L, "부산 해운대구 해운대로 1", "48000");

        assertThat(result.getAddress()).isEqualTo("부산 해운대구 해운대로 1");
        assertThat(result.getZipcode()).isEqualTo("48000");
    }

    @Test
    @DisplayName("존재하지 않는 주문 수정 시 예외")
    void modify_throwsException_whenOrderNotFound() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.modify(999L, "새 주소", "00000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("ORDERED가 아닌 주문 수정 시 예외")
    void modify_throwsException_whenNotOrdered() {
        Order confirmed = orderedOrder("conf@test.com");
        confirmed.updateStatus(OrderStatus.CONFIRMED);
        given(orderRepository.findById(1L)).willReturn(Optional.of(confirmed));

        assertThatThrownBy(() -> orderService.modify(1L, "새 주소", "00000"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문완료 상태의 주문만 수정할 수 있습니다.");
    }

    // ─── findByEmail() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("이메일로 주문 목록 조회")
    void findByEmail_returnsOrders() {
        List<Order> orders = List.of(orderedOrder("find@test.com"), orderedOrder("find@test.com"));
        given(orderRepository.findByEmail("find@test.com")).willReturn(orders);

        List<Order> result = orderService.findByEmail("find@test.com");

        assertThat(result).hasSize(2);
        verify(orderRepository).findByEmail("find@test.com");
    }
}
