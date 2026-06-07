package com.backend.domain.order.order.dto;

import java.util.List;

public record AdminOrderBulkShippedResponse(
        int processedCount,
        List<Long> orderIds
) {
    public static AdminOrderBulkShippedResponse from(List<Long> orderIds) {
        return new AdminOrderBulkShippedResponse(
                orderIds.size(),
                orderIds
        );
    }
}
