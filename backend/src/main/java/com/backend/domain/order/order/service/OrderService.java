package com.backend.domain.order.order.service;

import com.backend.domain.order.order.dto.OrderItemRequest;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Order save(String email, String address, String zipcode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.toLocalDate().atTime(14, 0);

        LocalDateTime shippingDate = now.isBefore(cutoff)
                ? now.toLocalDate().plusDays(1).atStartOfDay()
                : now.toLocalDate().plusDays(2).atStartOfDay();

        return orderRepository.save(new Order(email, shippingDate, address, zipcode, now));
    }

    public Order create(String email, String address, String zipcode, List<OrderItemRequest> items) {
        Order order = save(email, address, zipcode);
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + item.productId()));
            order.addUpdateOrderItem(product, item.quantity());
        }
        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + id));
    }

    public void cancel(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("이미 배송된 주문은 취소할 수 없습니다.");
        }
        orderRepository.delete(order);
    }

    public long count() {
        return orderRepository.count();
    }
}
