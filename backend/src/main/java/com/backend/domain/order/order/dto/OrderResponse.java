package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.orderItem.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String email,
        String address,
        String zipcode,
        LocalDateTime shippingDate,
        String status,
        LocalDateTime orderAt,
        List<OrderItemResponse> orderItems
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getZipcode(),
                order.getShippingDate(),
                order.getStatus().name(),
                order.getOrderAt(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }

    public record OrderItemResponse(
            Long id,
            ProductInfo product,
            int quantity
    ) {
        public static OrderItemResponse from(OrderItem orderItem) {
            return new OrderItemResponse(
                    orderItem.getId(),
                    new ProductInfo(
                            orderItem.getProduct().getId(),
                            orderItem.getProduct().getName(),
                            orderItem.getProduct().getPrice(),
                            orderItem.getProduct().getDescription(),
                            orderItem.getProduct().getImageUrl()
                    ),
                    orderItem.getQuantity()
            );
        }

        public record ProductInfo(
                Long id,
                String name,
                int price,
                String description,
                String imageUrl
        ) {}
    }
}
