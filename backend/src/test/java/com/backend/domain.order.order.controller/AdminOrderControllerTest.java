package com.backend.domain.order.order.controller;

import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.entity.OrderStatus;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminOrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long todayOrderId;
    private Long tomorrowOrderId;
    private Long shippedOrderId;

    @BeforeEach
    void setUp() {
        Product ethiopia = productRepository.save(new Product(
                "에티오피아 예가체프",
                32000,
                "꽃향과 산미가 특징인 싱글오리진 원두",
                "https://example.com/ethiopia.jpg"
        ));
        Product colombia = productRepository.save(new Product(
                "콜롬비아 수프리모",
                15000,
                "균형 잡힌 바디감과 고소한 풍미의 원두",
                "https://example.com/colombia.jpg"
        ));

        LocalDateTime now = LocalDateTime.now();

        Order todayOrder = new Order(
                "today@test.com",
                now.toLocalDate().atStartOfDay(),
                "서울 강남구 테헤란로 123",
                "06234",
                now.minusHours(1)
        );
        todayOrder.addUpdateOrderItem(ethiopia, 2);
        todayOrder.addUpdateOrderItem(colombia, 1);
        todayOrderId = orderRepository.save(todayOrder).getId();

        Order tomorrowOrder = new Order(
                "tomorrow@test.com",
                now.toLocalDate().plusDays(1).atStartOfDay(),
                "부산 해운대구 해운대로 456",
                "48094",
                now.minusHours(2)
        );
        tomorrowOrder.addUpdateOrderItem(colombia, 3);
        tomorrowOrderId = orderRepository.save(tomorrowOrder).getId();

        Order shippedOrder = new Order(
                "shipped@test.com",
                now.toLocalDate().atStartOfDay(),
                "대구 중구 중앙대로 100",
                "41911",
                now.minusHours(3)
        );
        shippedOrder.addUpdateOrderItem(ethiopia, 1);
        shippedOrder.updateStatus(OrderStatus.SHIPPED);
        shippedOrderId = orderRepository.save(shippedOrder).getId();

        Order deliveredOrder = new Order(
                "delivered@test.com",
                now.toLocalDate().atStartOfDay(),
                "광주 북구 무등로 200",
                "61234",
                now.minusHours(4)
        );
        deliveredOrder.addUpdateOrderItem(ethiopia, 1);
        deliveredOrder.updateStatus(OrderStatus.DELIVERED);
        orderRepository.save(deliveredOrder);

        Order canceledOrder = new Order(
                "canceled@test.com",
                now.toLocalDate().atStartOfDay(),
                "울산 남구 삼산로 300",
                "44705",
                now.minusHours(5)
        );
        canceledOrder.addUpdateOrderItem(colombia, 1);
        canceledOrder.updateStatus(OrderStatus.CANCELED);
        orderRepository.save(canceledOrder);
    }

    @Test
    @DisplayName("A-01: 관리자 주문 목록 조회 성공")
    void t1() throws Exception {
        mvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.data[0].email").exists())
                .andExpect(jsonPath("$.data[0].totalPrice").exists())
                .andExpect(jsonPath("$.data[0].status").exists());
    }

    @Test
    @DisplayName("A-02: status, keyword 파라미터가 있어도 주문 목록 조회 성공")
    void t2() throws Exception {
        mvc.perform(get("/api/admin/orders")
                        .param("status", "ORDERED")
                        .param("keyword", "today@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("A-03: 오늘 처리 주문 조회 성공")
    void t3() throws Exception {
        mvc.perform(get("/api/admin/orders/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("오늘 처리 주문 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data[*].email", hasItem("today@test.com")))
                .andExpect(jsonPath("$.data[*].email", hasItem("shipped@test.com")))
                .andExpect(jsonPath("$.data[*].email", not(hasItem("tomorrow@test.com"))))
                .andExpect(jsonPath("$.data[*].email", not(hasItem("delivered@test.com"))))
                .andExpect(jsonPath("$.data[*].email", not(hasItem("canceled@test.com"))));
    }

    @Test
    @DisplayName("A-04: 주문 상세 조회 성공")
    void t4() throws Exception {
        mvc.perform(get("/api/admin/orders/" + todayOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문 상세 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(todayOrderId))
                .andExpect(jsonPath("$.data.email").value("today@test.com"))
                .andExpect(jsonPath("$.data.status").value("ORDERED"))
                .andExpect(jsonPath("$.data.totalPrice").value(79000))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items", hasSize(2)))
                .andExpect(jsonPath("$.data.items[0].productId").exists())
                .andExpect(jsonPath("$.data.items[0].productName").exists())
                .andExpect(jsonPath("$.data.items[0].quantity").exists())
                .andExpect(jsonPath("$.data.items[0].orderPrice").exists())
                .andExpect(jsonPath("$.data.items[0].totalPrice").exists());
    }

    @Test
    @DisplayName("A-05: 단건 배송 완료 처리 성공")
    void t5() throws Exception {
        mvc.perform(put("/api/admin/orders/" + todayOrderId + "/shipped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("배송 완료 처리 성공"))
                .andExpect(jsonPath("$.data.id").value(todayOrderId))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("A-05-1: 단건 주문 상태 변경 성공")
    void t5_1() throws Exception {
        String requestBody = """
                {
                    "status": "CONFIRMED"
                }
                """;

        mvc.perform(patch("/api/admin/orders/" + todayOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문 상태 변경 성공"))
                .andExpect(jsonPath("$.data.id").value(todayOrderId))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("A-05-2: 주문 상태를 배송준비중으로 변경 성공")
    void t5_2() throws Exception {
        String requestBody = """
                {
                    "status": "PREPARING_SHIPMENT"
                }
                """;

        mvc.perform(patch("/api/admin/orders/" + todayOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문 상태 변경 성공"))
                .andExpect(jsonPath("$.data.id").value(todayOrderId))
                .andExpect(jsonPath("$.data.status").value("PREPARING_SHIPMENT"));
    }

    @Test
    @DisplayName("A-05-3: 잘못된 주문 상태로 변경 시 404")
    void t5_3() throws Exception {
        String requestBody = """
                {
                    "status": "INVALID_STATUS"
                }
                """;

        mvc.perform(patch("/api/admin/orders/" + todayOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404"))
                .andExpect(jsonPath("$.message").value("No enum constant com.backend.domain.order.order.entity.OrderStatus.INVALID_STATUS"));
    }

    @Test
    @DisplayName("A-05-4: 상태 변경 요청 본문이 비어 있으면 500")
    void t5_4() throws Exception {
        mvc.perform(patch("/api/admin/orders/" + todayOrderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("A-06: 일괄 배송 완료 처리 성공")
    void t6() throws Exception {
        String requestBody = """
                {
                    "orderIds": [%d, %d]
                }
                """.formatted(todayOrderId, tomorrowOrderId);

        mvc.perform(put("/api/admin/orders/shipped")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("일괄 배송 완료 처리 성공"))
                .andExpect(jsonPath("$.data.processedCount").value(2))
                .andExpect(jsonPath("$.data.orderIds").isArray())
                .andExpect(jsonPath("$.data.orderIds", hasSize(2)));
    }

    @Test
    @DisplayName("A-06-1: 빈 주문 목록 일괄 배송 완료 처리 시 processedCount 0")
    void t6_1() throws Exception {
        String requestBody = """
                {
                    "orderIds": []
                }
                """;

        mvc.perform(put("/api/admin/orders/shipped")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("일괄 배송 완료 처리 성공"))
                .andExpect(jsonPath("$.data.processedCount").value(0))
                .andExpect(jsonPath("$.data.orderIds").isArray())
                .andExpect(jsonPath("$.data.orderIds", hasSize(0)));
    }

    @Test
    @DisplayName("A-06-2: 일괄 배송 완료 처리 후 대상 주문 상태가 SHIPPED로 저장됨")
    void t6_2() throws Exception {
        String requestBody = """
                {
                    "orderIds": [%d, %d]
                }
                """.formatted(todayOrderId, tomorrowOrderId);

        mvc.perform(put("/api/admin/orders/shipped")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        orderRepository.flush();

        mvc.perform(get("/api/admin/orders/" + todayOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        mvc.perform(get("/api/admin/orders/" + tomorrowOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("A-07: 이미 배송 완료된 주문도 배송 완료 요청 시 200")
    void t7() throws Exception {
        mvc.perform(put("/api/admin/orders/" + shippedOrderId + "/shipped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("A-08: 존재하지 않는 주문 상세 조회 시 500")
    void t8() throws Exception {
        mvc.perform(get("/api/admin/orders/999999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("A-09: 존재하지 않는 주문 배송 완료 처리 시 500")
    void t9() throws Exception {
        mvc.perform(put("/api/admin/orders/999999/shipped"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("A-10: 일괄 배송 요청에 존재하지 않는 주문이 포함되면 500")
    void t10() throws Exception {
        String requestBody = """
                {
                    "orderIds": [%d, 999999]
                }
                """.formatted(todayOrderId);

        mvc.perform(put("/api/admin/orders/shipped")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"));
    }

    @Test
    @DisplayName("A-11: 잘못된 HTTP 메서드로 요청 시 500")
    void t11() throws Exception {
        mvc.perform(post("/api/admin/orders/" + todayOrderId + "/shipped"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"));
    }

    @Test
    @DisplayName("A-12: 주문 id 타입이 숫자가 아니면 500")
    void t12() throws Exception {
        mvc.perform(get("/api/admin/orders/not-number"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"));
    }
}
