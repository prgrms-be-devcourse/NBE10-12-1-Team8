package com.backend.domain.product.product.repository;

import com.backend.domain.product.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellingTrue();
}
