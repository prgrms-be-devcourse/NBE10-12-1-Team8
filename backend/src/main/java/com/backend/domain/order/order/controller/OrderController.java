package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.OrderCreateRequest;
import com.backend.domain.order.order.dto.OrderCreateResponse;
import com.backend.domain.order.order.dto.OrderModifyRequest;
import com.backend.domain.order.order.dto.OrderResponse;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.service.OrderService;
import com.backend.global.rsData.RsData;
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
    public ResponseEntity<RsData<List<OrderResponse>>> findByEmail(
            @RequestParam(required = false, defaultValue = "") String email
    ) {
        if (email.isBlank()) {
            return ResponseEntity.ok(RsData.of("200", "주문 목록 조회 성공", List.of()));
        }
        List<OrderResponse> orders = orderService.findByEmail(email).stream().map(OrderResponse::from).toList();
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

    @PutMapping("/{id}")
    public ResponseEntity<RsData<OrderCreateResponse>> modifyOrder(
            @PathVariable Long id,
            @RequestBody OrderModifyRequest request
    ) {

        Order order = orderService.modify(
                id,
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
}