package com.backend.domain.order.order.service;

import com.backend.domain.order.order.dto.OrderItemRequest;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private LocalDateTime calculateShippingDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.toLocalDate().atTime(14, 0);
        return now.isBefore(cutoff)
                ? now.toLocalDate().atStartOfDay()
                : now.toLocalDate().plusDays(1).atStartOfDay();
    }

    @Transactional
    public Order save(String email, String address, String zipcode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime shippingDate = calculateShippingDate();
        return orderRepository.save(new Order(email, shippingDate, address, zipcode, now));
    }

    @Transactional
    public Order create(String email, String address, String zipcode, List<OrderItemRequest> items) {
        LocalDateTime shippingDate = calculateShippingDate();

        if (orderRepository.findExistingOrder(email, shippingDate, address, zipcode).isPresent()) {
            throw new IllegalStateException("동일한 배송 정보로 이미 주문이 존재합니다. 기존 주문을 취소 후 다시 주문해주세요.");
        }

        Order order = new Order(email, shippingDate, address, zipcode, LocalDateTime.now());
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + item.productId()));
            if (!product.isSelling()) {
                throw new IllegalStateException("판매중지된 상품은 주문할 수 없습니다.");
            }
            order.addUpdateOrderItem(product, item.quantity());
        }
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public long count() {
        return orderRepository.count();
    }

    @Transactional(readOnly = true)
   public List<Order> findByEmail(String email){
       return orderRepository.findByEmail(email);
   }

   @Transactional
   public void cancel(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
       if (order.getStatus() != OrderStatus.ORDERED) {
           throw new IllegalStateException("주문완료 상태의 주문만 취소할 수 있습니다.");
       }
       order.updateStatus(OrderStatus.CANCELED);
   }
   @Transactional
    public Order modify(Long id, String address, String zipcode) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("주문완료 상태의 주문만 수정할 수 있습니다.");
        }

        order.modify(address, zipcode);

        return orderRepository.save(order);
    }
}
