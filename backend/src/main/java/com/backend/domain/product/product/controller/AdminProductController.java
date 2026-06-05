package com.backend.domain.product.product.controller;

import com.backend.domain.product.product.dto.AdminProductCreateRequest;
import com.backend.domain.product.product.dto.AdminProductDetailResponse;
import com.backend.domain.product.product.dto.AdminProductResponse;
import com.backend.domain.product.product.dto.AdminProductUpdateRequest;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.AdminProductService;
import com.backend.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<RsData<List<AdminProductResponse>>> getProducts() {
        List<AdminProductResponse> products = adminProductService.findAll()
                .stream()
                .map(AdminProductResponse::from)
                .toList();
        return ResponseEntity.ok(RsData.of("200", "상품 목록 조회 성공", products));
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<RsData<AdminProductDetailResponse>> getProduct(@PathVariable Long id) {
        Product product = adminProductService.findById(id);
        return ResponseEntity.ok(RsData.of("200", "상품 조회 성공", AdminProductDetailResponse.from(product)));
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<RsData<AdminProductResponse>> createProduct(@Valid @RequestBody AdminProductCreateRequest request) {
        Product product = adminProductService.save(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201", "상품 등록 성공", AdminProductResponse.from(product)));
    }

    // 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<RsData<AdminProductResponse>> updateProduct(
            @PathVariable Long id,
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

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<Void>> deleteProduct(@PathVariable Long id) {
        adminProductService.delete(id);
        return ResponseEntity.ok(RsData.of("200", "상품 삭제 성공"));
    }
}
