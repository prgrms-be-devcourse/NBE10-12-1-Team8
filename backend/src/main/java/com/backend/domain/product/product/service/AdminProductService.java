package com.backend.domain.product.product.service;

import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {
    private final ProductRepository productRepository;

    public Product save(String name, int price, String description, String imageUrl) {
        return productRepository.save(new Product(name, price, description, imageUrl));

    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));
    }

    public Product modify(Long id, String name, int price, String description, String imageUrl) {
        Product product = findById(id);
        product.modify(name, price, description, imageUrl);
        return productRepository.save(product);
    }

    public Product updateSalesStatus(Long id, boolean selling) {
        Product product = findById(id);
        product.updateSelling(selling);
        return productRepository.save(product);
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }




}
