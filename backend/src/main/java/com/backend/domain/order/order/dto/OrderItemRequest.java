package com.backend.domain.order.order.dto;

public record OrderItemRequest(Long productId, int quantity) {}