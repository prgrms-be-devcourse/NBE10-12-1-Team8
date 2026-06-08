package com.backend.domain.order.order.dto;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.orderItem.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse (
    Long orderId,
    String email,
    String address,
    String zipcode,
    LocalDateTime shippingDate,
    String status,
    int totalPrice,
    List<OrderItemResponse>items
){
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getZipcode(),
                order.getShippingDate(),
                order.getStatus().name(),
                order.calculateTotalPrice(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }

    public record OrderItemResponse(
            String productName,
            int quantity,
            int totalPrice
    ){
        public static OrderItemResponse from(OrderItem orderItem) {
            return new OrderItemResponse(
                    orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    orderItem.calculateTotalPrice()
            );
        }
    }
}

