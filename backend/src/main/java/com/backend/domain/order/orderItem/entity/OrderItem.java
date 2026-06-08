package com.backend.domain.order.orderItem.entity;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.product.product.entity.Product;
import com.backend.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "order_item",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id","product_id"})
        }
)
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public OrderItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity){
        this.quantity += quantity;
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public boolean isSameProduct(Product product){
        return this.product.getId().equals(product.getId());
    }
    public int calculateTotalPrice(){
        return product.getPrice() * quantity;
    }
}
