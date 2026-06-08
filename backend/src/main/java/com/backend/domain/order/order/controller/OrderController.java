package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.OrderCreateRequest;
import com.backend.domain.order.order.dto.OrderCreateResponse;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.service.OrderService;
import com.backend.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<RsData<OrderCreateResponse>> createOrder(
            @RequestBody OrderCreateRequest request
    ) {

        Order order = orderService.create(
                request.email(),
                request.address(),
                request.zipcode(),
                request.items()
        );

        return ResponseEntity.ok(
                RsData.of(
                        "201",
                        "주문이 생성되었습니다.",
                        OrderCreateResponse.from(order)
                )
        );
    }

    @GetMapping
    public ResponseEntity<RsData<List<Order>>> findByEmail(
            @RequestParam String email
    ) {
        List<Order> orders = orderService.findByEmail(email);
        return ResponseEntity.ok(
                RsData.of(
                        "200",
                        "주문 목록 조회 성공",
                        orders
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<Void>> deleteOrder(
            @PathVariable Long id
    ) {
        orderService.cancel(id);
        return ResponseEntity.ok(
                RsData.of(
                        "200",
                        "주문이 취소되었습니다."
                )
        );
    }
}