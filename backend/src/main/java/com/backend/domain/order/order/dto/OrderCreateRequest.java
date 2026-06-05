package com.backend.domain.order.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

        @NotBlank(message = "우편번호는 필수입니다.")
        String zipcode,

        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다.")
        List<OrderItemRequest> items
) {}
