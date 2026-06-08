package com.backend.domain.product.product.dto;

public record AdminProductImageUploadResponse(
        String imageUrl
) {
    public static AdminProductImageUploadResponse from(String imageUrl) {
        return new AdminProductImageUploadResponse(imageUrl);
    }
}
