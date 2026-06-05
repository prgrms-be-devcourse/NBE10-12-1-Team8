package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.*;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.service.AdminOrderService;
import com.backend.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<RsData<List<AdminOrderResponse>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        List<AdminOrderResponse> orders = adminOrderService.findAll(status, keyword)
                .stream()
                .map(AdminOrderResponse::from)
                .toList();
        return ResponseEntity.ok(RsData.of("200", "주문 목록 조회 성공", orders));
    }

    @GetMapping("/today")
    public ResponseEntity<RsData<List<AdminOrderResponse>>> getTodayOrders() {
        List<AdminOrderResponse> orders = adminOrderService.findTodayOrders()
                .stream()
                .map(AdminOrderResponse::from)
                .toList();
        return ResponseEntity.ok(RsData.of("200", "오늘의 배송 주문 조회 성공", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsData<AdminOrderDetailResponse>> getOrder(@PathVariable Long id) {
        Order order = adminOrderService.findById(id);
        return ResponseEntity.ok(RsData.of("200", "주문 상세 조회 성공", AdminOrderDetailResponse.from(order)));
    }

    @PutMapping("/{id}/shipped")
    public ResponseEntity<RsData<AdminOrderStatusResponse>> shipOrder(@PathVariable Long id) {
        Order order = adminOrderService.updateShipped(id);
        return ResponseEntity.ok(RsData.of("200", "주문 배송 처리 성공", AdminOrderStatusResponse.from(order)));
    }

    @PutMapping("/shipped")
    public ResponseEntity<RsData<AdminOrderBulkShippedResponse>> shipOrders(
            @RequestBody AdminOrderBulkShippedRequest request) {
        List<Order> orders = adminOrderService.updateBulkShipped(request.orderIds());
        List<Long> processedIds = orders.stream().map(Order::getId).toList();
        return ResponseEntity.ok(RsData.of("200", "일괄 배송 처리 성공", AdminOrderBulkShippedResponse.from(processedIds)));
    }
}
