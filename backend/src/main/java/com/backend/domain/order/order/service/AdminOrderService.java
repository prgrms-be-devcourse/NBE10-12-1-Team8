package com.backend.domain.order.order.service;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
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
                .filter(order -> isTodayVisibleStatus(order.getStatus()))
                .filter(order -> !order.getShippingDate().toLocalDate().isAfter(today))
                .toList();
    }
    public Order updateShipped(Long id) {
        Order order = findById(id);
        order.updateStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    public Order updateStatus(Long id, String status) {
        Order order = findById(id);
        OrderStatus nextStatus = OrderStatus.valueOf(status);
        order.updateStatus(nextStatus);
        return orderRepository.save(order);
    }

    public List<Order> updateBulkShipped(List<Long> ids) {
        return ids.stream()
                .map(this::updateShipped)
                .toList();
    }

    private boolean isTodayVisibleStatus(OrderStatus status) {
        return status != OrderStatus.CANCELED;
    }
}
