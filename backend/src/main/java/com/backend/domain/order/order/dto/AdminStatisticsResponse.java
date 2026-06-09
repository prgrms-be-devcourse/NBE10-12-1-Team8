package com.backend.domain.order.order.dto;

import java.util.List;

public record AdminStatisticsResponse(
        List<MonthlySalesResponse> monthlySales,
        List<DailySalesResponse> recentDailySales,
        List<ProductSalesResponse> productSales
) {
    public record MonthlySalesResponse(
            String month,
            int totalSales
    ) {
    }

    public record DailySalesResponse(
            String date,
            int totalSales
    ) {
    }

    public record ProductSalesResponse(
            Long productId,
            String productName,
            int quantity
    ) {
    }
}
