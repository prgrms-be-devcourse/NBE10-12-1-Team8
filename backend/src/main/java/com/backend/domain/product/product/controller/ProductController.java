package com.backend.domain.product.product.controller;

import com.backend.domain.product.product.dto.ProductResponse;
import com.backend.domain.product.product.service.ProductService;
import com.backend.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<RsData<List<ProductResponse>>> getProducts() {
        List<ProductResponse> products = productService.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.ok(RsData.of("200", "상품 목록 조회 성공", products));
    }
}
