package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;

public record AdminOrderStatusResponse(
        Long id,
        String status
) {
    public static AdminOrderStatusResponse from(Order order) {
        return new AdminOrderStatusResponse(
                order.getId(),
                order.getStatus().name()
        );
    }
}
