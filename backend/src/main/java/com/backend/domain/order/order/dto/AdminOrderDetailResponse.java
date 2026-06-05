package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.orderItem.entity.OrderItem;

import java.util.List;

public record AdminOrderDetailResponse(
        Long id,
        String email,
        String shippingDate,
        String address,
        String zipcode,
        String orderAt,
        String status,
        int totalPrice,
        List<AdminOrderItemResponse> items
) {
    public static AdminOrderDetailResponse from(Order order) {
        return new AdminOrderDetailResponse(
                order.getId(),
                order.getEmail(),
                order.getShippingDate().toString(),
                order.getAddress(),
                order.getZipcode(),
                order.getOrderAt().toString(),
                order.getStatus().name(),
                order.calculateTotalPrice(),
                order.getOrderItems().stream()
                        .map(AdminOrderItemResponse::from)
                        .toList()
        );
    }

    public record AdminOrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            int orderPrice,
            int totalPrice
    ) {
        public static AdminOrderItemResponse from(OrderItem orderItem) {
            return new AdminOrderItemResponse(
                    orderItem.getProduct().getId(),
                    orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    orderItem.getProduct().getPrice(),
                    orderItem.calculateTotalPrice()
            );
        }
    }
}
