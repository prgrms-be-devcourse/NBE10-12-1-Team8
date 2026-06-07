package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;

import java.time.LocalDateTime;

public record OrderCreateResponse(
        Long orderId,
        String email,
        String address,
        String zipcode,
        LocalDateTime shippingDate,
        int totalPrice
) {

    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getZipcode(),
                order.getShippingDate(),
                order.calculateTotalPrice()
        );
    }
}