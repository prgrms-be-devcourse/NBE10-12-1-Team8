package com.backend.domain.order.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public void getOrders(@RequestParam(required = false) String status,
                          @RequestParam(required = false) String keyword) {}

    @GetMapping("/today")
    public void getTodayOrders() {}

    @GetMapping("/{id}")
    public void getOrder(@PathVariable Long id) {}

    @PutMapping("/{id}/shipped")
    public void shipOrder(@PathVariable Long id) {}

    @PutMapping("/shipped")
    public void shipOrders(@RequestBody AdminOrderBulkShippedRequest request) {}
}
