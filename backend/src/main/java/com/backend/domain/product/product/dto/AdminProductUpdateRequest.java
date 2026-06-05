package com.backend.domain.product.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProductUpdateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String description;

    private String imageUrl;
}
