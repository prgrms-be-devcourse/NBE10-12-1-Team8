package com.backend.domain.product.product.service;

import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));
    }

    public long count() {
        return productRepository.count();
    }

    public Product create(String name, int price, String description, String imageUrl) {
        return productRepository.save(new Product(name, price, description, imageUrl));
    }
}
