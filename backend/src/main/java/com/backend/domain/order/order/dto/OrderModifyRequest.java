package com.backend.domain.order.order.dto;

public record OrderModifyRequest(
        String address,
        String zipcode
) {
}