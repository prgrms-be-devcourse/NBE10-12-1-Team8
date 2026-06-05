package com.backend.domain.product.product.service;

import com.backend.domain.product.product.entity.Product;

import java.util.Optional;

public class ProductService {
    private ProductRepository productRepository;

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }
}
