package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.OrderCreateRequest;
import com.backend.domain.order.order.dto.OrderResponse;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.service.OrderService;
import com.backend.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<RsData<OrderResponse>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order order = orderService.create(request.email(), request.address(), request.zipcode(), request.items());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.of("201", "주문이 성공적으로 완료되었습니다.", OrderResponse.from(order)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsData<OrderResponse>> getOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(RsData.of("200", "주문 조회 성공", OrderResponse.from(order)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.ok(RsData.of("200", "주문이 취소되었습니다."));
    }
}
