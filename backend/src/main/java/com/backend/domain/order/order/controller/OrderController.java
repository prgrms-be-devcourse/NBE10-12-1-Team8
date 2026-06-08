package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.OrderCreateRequest;
import com.backend.domain.order.order.dto.OrderCreateResponse;
import com.backend.domain.order.order.dto.OrderModifyRequest;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.service.OrderService;
import com.backend.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PatchMapping("/{orderId}")
    public ResponseEntity<RsData<OrderCreateResponse>> modify(
            @PathVariable Long orderId,
            @RequestBody OrderModifyRequest request
    ) {

        Order order = orderService.modify(
                orderId,
                request.address(),
                request.zipcode()
        );

        return ResponseEntity.ok(
                RsData.of(
                        "200",
                        "주문이 수정되었습니다.",
                        OrderCreateResponse.from(order)
                )
        );
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<RsData<OrderCreateResponse>> getOrder(
            @PathVariable Long orderId
    ) {
        Order order = orderService.findById(orderId);

        return ResponseEntity.ok(
                RsData.of(
                        "200",
                        "주문 조회 성공",
                        OrderCreateResponse.from(order)
                )
        );
    }
}