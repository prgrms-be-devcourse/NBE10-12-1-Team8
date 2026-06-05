package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;

public record AdminOrderResponse(
        Long id,
        String email,
        String shippingDate,
        String address,
        String zipcode,
        int totalPrice,
        String status,
        String orderAt
) {
    public static AdminOrderResponse from(Order order) {
        return new AdminOrderResponse(
                order.getId(),
                order.getEmail(),
                order.getShippingDate().toString(),
                order.getAddress(),
                order.getZipcode(),
                order.calculateTotalPrice(),
                order.getStatus().name(),
                order.getOrderAt().toString()
        );
    }
}
