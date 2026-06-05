package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.AdminOrderBulkShippedResponse;
import com.backend.domain.order.order.dto.AdminOrderBulkShippedRequest;
import com.backend.domain.order.order.dto.AdminOrderDetailResponse;
import com.backend.domain.order.order.dto.AdminOrderResponse;
import com.backend.domain.order.order.dto.AdminOrderStatusResponse;
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
            @RequestParam(required = false) String keyword
    ) {
        List<AdminOrderResponse> orders = adminOrderService.findAll().stream()
                .map(AdminOrderResponse::from)
                .toList();

        return ResponseEntity.ok(RsData.of("200", "주문 목록 조회 성공", orders));
    }

    @GetMapping("/today")
    public ResponseEntity<RsData<List<AdminOrderResponse>>> getTodayOrders() {
        List<AdminOrderResponse> orders = adminOrderService.findTodayOrders().stream()
                .map(AdminOrderResponse::from)
                .toList();

        return ResponseEntity.ok(RsData.of("200", "오늘 처리 주문 조회 성공", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsData<AdminOrderDetailResponse>> getOrder(@PathVariable Long id) {
        AdminOrderDetailResponse order = AdminOrderDetailResponse.from(adminOrderService.findById(id));

        return ResponseEntity.ok(RsData.of("200", "주문 상세 조회 성공", order));
    }

    @PutMapping("/{id}/shipped")
    public ResponseEntity<RsData<AdminOrderStatusResponse>> shipOrder(@PathVariable Long id) {
        AdminOrderStatusResponse order = AdminOrderStatusResponse.from(adminOrderService.updateShipped(id));

        return ResponseEntity.ok(RsData.of("200", "배송 완료 처리 성공", order));
    }

    @PutMapping("/shipped")
    public ResponseEntity<RsData<AdminOrderBulkShippedResponse>> shipOrders(
            @RequestBody AdminOrderBulkShippedRequest request
    ) {
        List<Long> shippedOrderIds = adminOrderService.updateBulkShipped(request.orderIds()).stream()
                .map(order -> order.getId())
                .toList();

        AdminOrderBulkShippedResponse response = AdminOrderBulkShippedResponse.from(shippedOrderIds);

        return ResponseEntity.ok(RsData.of("200", "일괄 배송 완료 처리 성공", response));
    }
}
