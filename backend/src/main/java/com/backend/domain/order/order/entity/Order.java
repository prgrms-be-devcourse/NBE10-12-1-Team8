package com.backend.domain.order.order.entity;


import com.backend.domain.order.orderItem.entity.OrderItem;
import com.backend.domain.product.product.entity.Product;
import com.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "email", "shipping_date", "zipcode", "address"
                })
        }
)
@NoArgsConstructor
public class Order extends BaseEntity {
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime shippingDate;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 20)
    private String zipcode;

    @Column(nullable = false)
    private LocalDateTime orderAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(String email, LocalDateTime shippingDate, String address, String zipcode, LocalDateTime orderAt){
        this.email =  email;
        this.shippingDate = shippingDate;
        this.address = address;
        this.zipcode = zipcode;
        this.orderAt = orderAt;
        this.status = OrderStatus.ORDERED;
    }

    public void addUpdateOrderItem(Product product, int quantity){
        for(OrderItem orderItem : orderItems){
            if(orderItem.isSameProduct(product)){
                orderItem.increaseQuantity(quantity);
                return;
            }
        }
        OrderItem newOrderItem = new OrderItem(product, quantity);
        addOrderItem(newOrderItem);
    }

    private void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public int calculateTotalPrice(){
        return orderItems.stream()
                .mapToInt(OrderItem::calculateTotalPrice)
                .sum();
    }

    public void updateStatus(OrderStatus status){
        this.status = status;
    }
    public void modify(String address, String zipcode) {
        this.address = address;
        this.zipcode = zipcode;
    }
}
