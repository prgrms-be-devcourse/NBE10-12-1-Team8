package com.backend.domain.order.order.service;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private  final OrderRepository orderRepository;

    public Order findById(Long id){
        return orderRepository.findById(id).orElseThrow();
    }
    public List<Order> findAll(){
        return orderRepository.findAll();
    }

    public List<Order> findTodayOrders() {
        LocalDate today = LocalDate.now();
        return orderRepository.findAll().stream()
                .filter(order -> order.getShippingDate().toLocalDate().equals(today))
                .toList();
    }
    public Order updateShipped(Long id) {
        Order order = findById(id);
        // TODO: order.updateStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    public List<Order> updateBulkShipped(List<Long> ids) {
        // TODO: 각 id별로 updateShipped 호출
        return List.of();
    }
}
