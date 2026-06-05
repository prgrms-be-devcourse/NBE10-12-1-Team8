package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.orderItem.entity.OrderItem;

import java.util.List;

public record OrderResponse(
        Long id,
        String email,
        String shippingDate,
        String address,
        String zipcode,
        String orderAt,
        String status,
        int totalPrice,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEmail(),
                order.getShippingDate().toString(),
                order.getAddress(),
                order.getZipcode(),
                order.getOrderAt().toString(),
                order.getStatus().name(),
                order.calculateTotalPrice(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            int price,
            int totalPrice
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.calculateTotalPrice()
            );
        }
    }
}
