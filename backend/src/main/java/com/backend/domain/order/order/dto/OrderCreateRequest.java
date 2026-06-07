package com.backend.domain.order.order.dto;

import java.util.List;

public record OrderCreateRequest(
        String email,
        String address,
        String zipcode,
        List<OrderItemRequest> items
) {
}