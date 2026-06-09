package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.dto.AdminStatisticsResponse;
import com.backend.domain.order.order.service.AdminStatisticsService;
import com.backend.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 통계 API", description = "관리자 매출 및 상품 판매 통계")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {
    private final AdminStatisticsService adminStatisticsService;

    @Operation(summary = "관리자 통계 조회", description = "월별 매출, 최근 7일 매출 추이, 상품별 판매량을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<RsData<AdminStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(
                RsData.of("200", "관리자 통계 조회 성공", adminStatisticsService.getStatistics())
        );
    }
}
