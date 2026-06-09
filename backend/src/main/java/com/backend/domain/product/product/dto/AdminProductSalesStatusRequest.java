package com.backend.domain.product.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "상품 판매상태 변경 요청")
public record AdminProductSalesStatusRequest(
        @Schema(description = "판매중 여부", example = "false")
        @NotNull(message = "판매상태는 필수입니다.")
        Boolean selling
) {
}
