package com.backend.domain.order.order.service;

import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.product.product.repository.ProductRepository;
import com.backend.domain.order.order.entity.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Order save(String email, String address, String zipcode){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.toLocalDate().atTime(14,0);

        //shippingDate 계산
        LocalDateTime shippingDate = now.isBefore(cutoff)
                ? now.toLocalDate().plusDays(1).atStartOfDay()
                : now.toLocalDate().plusDays(2).atStartOfDay();

        return orderRepository.save(new Order(email, shippingDate, address, zipcode, now));
    }



}
