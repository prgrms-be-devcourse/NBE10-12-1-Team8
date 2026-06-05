package com.backend.domain.order.order.dto;

import java.util.List;

public record AdminOrderBulkShippedRequest(
        List<Long> orderIds
) {
}
