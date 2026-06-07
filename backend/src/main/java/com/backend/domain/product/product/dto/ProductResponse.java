package com.backend.domain.product.product.dto;

import com.backend.domain.product.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final String description;
    private final String imageUrl;

    private ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(product);
    }
}
