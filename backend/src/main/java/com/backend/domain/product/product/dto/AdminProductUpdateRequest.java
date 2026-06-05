package com.backend.domain.product.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "상품 수정 요청")
@Getter
@NoArgsConstructor
public class AdminProductUpdateRequest {

    @Schema(description = "상품명", example = "콜롬비아 수프리모")
    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Schema(description = "가격 (0 이상)", example = "28000")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @Schema(description = "상품 설명", example = "고소한 향과 균형 잡힌 맛")
    private String description;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/colombia.jpg")
    private String imageUrl;
}
