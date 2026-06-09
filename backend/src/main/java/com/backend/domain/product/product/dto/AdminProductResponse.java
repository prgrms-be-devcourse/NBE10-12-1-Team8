package com.backend.domain.product.product.dto;

import com.backend.domain.product.product.entity.Product;
import lombok.Getter;

@Getter
public class AdminProductResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final String description;
    private final String imageUrl;
    private final boolean selling;

    private AdminProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.selling = product.isSelling();
    }

    public static AdminProductResponse from(Product product) {
        return new AdminProductResponse(product);
    }
}
