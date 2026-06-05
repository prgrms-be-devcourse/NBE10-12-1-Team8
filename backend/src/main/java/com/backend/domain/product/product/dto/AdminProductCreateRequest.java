package com.backend.domain.product.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "상품 등록 요청")
@Getter
@NoArgsConstructor
public class AdminProductCreateRequest {

    @Schema(description = "상품명", example = "에티오피아 예가체프")
    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Schema(description = "가격 (0 이상)", example = "32000")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @Schema(description = "상품 설명", example = "꽃향과 산미가 특징인 싱글오리진 원두")
    private String description;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/ethiopia.jpg")
    private String imageUrl;
}
