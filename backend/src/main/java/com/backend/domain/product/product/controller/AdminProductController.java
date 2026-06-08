package com.backend.domain.product.product.controller;

import com.backend.domain.product.product.dto.AdminProductCreateRequest;
import com.backend.domain.product.product.dto.AdminProductDetailResponse;
import com.backend.domain.product.product.dto.AdminProductImageUploadResponse;
import com.backend.domain.product.product.dto.AdminProductResponse;
import com.backend.domain.product.product.dto.AdminProductUpdateRequest;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.AdminProductService;
import com.backend.domain.product.product.service.ProductImageStorageService;
import com.backend.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "관리자 상품 API", description = "관리자 상품 등록/조회/수정/삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final ProductImageStorageService productImageStorageService;

    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<RsData<List<AdminProductResponse>>> getProducts() {
        List<AdminProductResponse> products = adminProductService.findAll()
                .stream()
                .map(AdminProductResponse::from)
                .toList();
        return ResponseEntity.ok(RsData.of("200", "상품 목록 조회 성공", products));
    }

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RsData<AdminProductDetailResponse>> getProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id) {
        Product product = adminProductService.findById(id);
        return ResponseEntity.ok(RsData.of("200", "상품 조회 성공", AdminProductDetailResponse.from(product)));
    }

    @Operation(summary = "상품 등록", description = "새 상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패")
    })
    @PostMapping
    public ResponseEntity<RsData<AdminProductResponse>> createProduct(
            @Valid @RequestBody AdminProductCreateRequest request) {
        Product product = adminProductService.save(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201", "상품 등록 성공", AdminProductResponse.from(product)));
    }

    @Operation(summary = "상품 이미지 업로드", description = "상품 이미지를 업로드하고 접근 가능한 이미지 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이미지 파일")
    })
    @PostMapping("/images")
    public ResponseEntity<RsData<AdminProductImageUploadResponse>> uploadProductImage(
            @RequestParam("image") MultipartFile image) {
        String imageUrl = productImageStorageService.store(image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201", "상품 이미지 업로드 성공", AdminProductImageUploadResponse.from(imageUrl)));
    }

    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RsData<AdminProductResponse>> updateProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AdminProductUpdateRequest request) {
        Product product = adminProductService.modify(
                id,
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl()
        );
        return ResponseEntity.ok(RsData.of("200", "상품 수정 성공", AdminProductResponse.from(product)));
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<Void>> deleteProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long id) {
        adminProductService.delete(id);
        return ResponseEntity.ok(RsData.of("200", "상품 삭제 성공"));
    }
}
