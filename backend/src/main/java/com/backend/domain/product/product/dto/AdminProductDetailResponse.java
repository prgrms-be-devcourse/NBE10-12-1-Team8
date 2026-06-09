package com.backend.domain.product.product.dto;

import com.backend.domain.product.product.entity.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminProductDetailResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final String description;
    private final String imageUrl;
    private final boolean selling;
    private final LocalDateTime createDate;
    private final LocalDateTime modifyDate;

    private AdminProductDetailResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.selling = product.isSelling();
        this.createDate = product.getCreateDate();
        this.modifyDate = product.getModifyDate();
    }

    public static AdminProductDetailResponse from(Product product) {
        return new AdminProductDetailResponse(product);
    }
}
