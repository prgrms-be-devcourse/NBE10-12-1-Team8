package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.AdminOrderBulkShippedResponse;
import com.backend.domain.order.order.dto.AdminOrderBulkShippedRequest;
import com.backend.domain.order.order.dto.AdminOrderDetailResponse;
import com.backend.domain.order.order.dto.AdminOrderResponse;
import com.backend.domain.order.order.dto.AdminOrderStatusResponse;
import com.backend.domain.order.order.dto.AdminOrderStatusUpdateRequest;
import com.backend.domain.order.order.service.AdminOrderService;
import com.backend.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 주문 API", description = "관리자 주문 조회 및 배송 처리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "주문 목록 조회", description = "전체 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<RsData<List<AdminOrderResponse>>> getOrders(
            @Parameter(description = "주문 상태", example = "ORDERED")
            @RequestParam(required = false) String status,
            @Parameter(description = "검색어", example = "test@example.com")
            @RequestParam(required = false) String keyword
    ) {
        List<AdminOrderResponse> orders = adminOrderService.findAll().stream()
                .map(AdminOrderResponse::from)
                .toList();

        return ResponseEntity.ok(RsData.of("200", "주문 목록 조회 성공", orders));
    }

    @Operation(summary = "오늘 처리 주문 조회", description = "배송 처리 예정일이 오늘인 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/today")
    public ResponseEntity<RsData<List<AdminOrderResponse>>> getTodayOrders() {
        List<AdminOrderResponse> orders = adminOrderService.findTodayOrders().stream()
                .map(AdminOrderResponse::from)
                .toList();

        return ResponseEntity.ok(RsData.of("200", "오늘 처리 주문 조회 성공", orders));
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보와 주문 상품 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "주문 없음 또는 서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RsData<AdminOrderDetailResponse>> getOrder(
            @Parameter(description = "주문 ID", example = "1") @PathVariable Long id) {
        AdminOrderDetailResponse order = AdminOrderDetailResponse.from(adminOrderService.findById(id));

        return ResponseEntity.ok(RsData.of("200", "주문 상세 조회 성공", order));
    }

    @Operation(summary = "단건 배송 완료 처리", description = "주문 1건의 상태를 SHIPPED로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 완료 처리 성공"),
            @ApiResponse(responseCode = "500", description = "주문 없음 또는 서버 오류")
    })
    @PutMapping("/{id}/shipped")
    public ResponseEntity<RsData<AdminOrderStatusResponse>> shipOrder(
            @Parameter(description = "주문 ID", example = "1") @PathVariable Long id) {
        AdminOrderStatusResponse order = AdminOrderStatusResponse.from(adminOrderService.updateShipped(id));

        return ResponseEntity.ok(RsData.of("200", "배송 완료 처리 성공", order));
    }

    @Operation(summary = "주문 상태 변경", description = "주문 1건의 상태를 지정한 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "500", description = "주문 없음 또는 서버 오류")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RsData<AdminOrderStatusResponse>> updateOrderStatus(
            @Parameter(description = "주문 ID", example = "1") @PathVariable Long id,
            @RequestBody AdminOrderStatusUpdateRequest request
    ) {
        AdminOrderStatusResponse order = AdminOrderStatusResponse.from(
                adminOrderService.updateStatus(id, request.status())
        );

        return ResponseEntity.ok(RsData.of("200", "주문 상태 변경 성공", order));
    }

    @Operation(summary = "일괄 배송 완료 처리", description = "여러 주문의 상태를 SHIPPED로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일괄 배송 완료 처리 성공"),
            @ApiResponse(responseCode = "500", description = "주문 없음 또는 서버 오류")
    })
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
