package com.backend.domain.order.order.repository;

import com.backend.domain.order.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEmail(String email);
    @Query("SELECT o FROM Order o WHERE o.email = :email AND o.shippingDate = :shippingDate AND o.address = :address AND o.zipcode = :zipcode")
    Optional<Order> findExistingOrder(
            @Param("email") String email,
            @Param("shippingDate") LocalDateTime shippingDate,
            @Param("address") String address,
            @Param("zipcode") String zipcode
    );
}
