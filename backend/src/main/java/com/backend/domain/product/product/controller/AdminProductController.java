package com.backend.domain.product.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public void getProducts() {}

    @GetMapping("/{id}")
    public void getProduct(@PathVariable Long id) {}

    @PostMapping
    public void createProduct(@RequestBody AdminProductCreateRequest request) {}

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id, @RequestBody AdminProductUpdateRequest request) {}

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {}
}
