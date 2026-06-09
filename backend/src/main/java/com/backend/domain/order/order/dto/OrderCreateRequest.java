package com.backend.domain.order.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String address,
        @NotBlank
        String zipcode,
        @Valid
        @NotEmpty
        List<OrderItemRequest> items
) {
}