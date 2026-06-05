package com.backend.domain.product.product.service;

import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProductService {
    private final ProductRepository productRepository;

    public Product save(String name, int price, String description, String imageUrl) {
        return productRepository.save(new Product(name, price, description, imageUrl));

    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    public Product modify(Long id, String name, int price, String description, String imageUrl) {
        Product product = findById(id);
        product.modify(name, price, description, imageUrl);
        return productRepository.save(product);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }



}
