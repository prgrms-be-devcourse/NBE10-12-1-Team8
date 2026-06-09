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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MutableClock clock;

    private Long orderdOrderId;
    private Long shippedOrderId;
    private Long productId;

    @TestConfiguration
    static class ClockTestConfig {
        @Bean
        @Primary
        MutableClock mutableClock() {
            return new MutableClock(ZoneId.of("Asia/Seoul"));
        }
    }

    static class MutableClock extends Clock {
        private final ZoneId zone;
        private Instant instant;

        MutableClock(ZoneId zone) {
            this.zone = zone;
            set(LocalDateTime.of(2026, 6, 9, 10, 0));
        }

        void set(LocalDateTime dateTime) {
            this.instant = dateTime.atZone(zone).toInstant();
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    @BeforeEach
   public void setUp(){
        clock.set(LocalDateTime.of(2026, 6, 9, 10, 0));

        Product ethiopia = productRepository.save(new Product(
                "에티오피아 예가체프",
                32000,
                "꽃향과 산미가 특징인 싱글오리진 원두",
                "https://example.com/ethiopia.jpg"
        ));
        productId = ethiopia.getId();
        Product colombia = productRepository.save(new Product(
                "콜롬비아 수프리모",
                15000,
                "균형 잡힌 바디감과 고소한 풍미의 원두",
                "https://example.com/colombia.jpg"
        ));

        LocalDateTime now = LocalDateTime.now();

        Order orderdOrder = new Order(
                "today@test.com",
                now.toLocalDate().atStartOfDay(),
                "서울 강남구 테헤란로 123",
                "06234",
                now.minusHours(1)
        );
        orderdOrder.addUpdateOrderItem(colombia, 1);
        orderdOrderId = orderRepository.save(orderdOrder).getId();

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
	   }

    private String orderRequest(String email, String address, String zipcode) {
        return """
                {
                   "email" : "%s",
                   "address" : "%s",
                   "zipcode": "%s",
                   "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(email, address, zipcode, productId);
    }

    @Test
    @DisplayName("주문 목록 조회")
    void t1() throws Exception{
        ResultActions resultActions = mvc
                .perform(
                        get("/api/orders").param("email", "today@test.com")

                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("findByEmail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].email").value("today@test.com"))
                .andExpect(jsonPath("$.data[0].status").exists())
                .andExpect(jsonPath("$.data[0].orderItems").isArray());

    }
    @Test
    @DisplayName("주문 취소 성공")
    public void t2() throws Exception{
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/orders/{orderId}", orderdOrderId)

                ).andDo(print());
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("주문이 취소되었습니다."));

        Order canceledOrder = orderRepository.findById(orderdOrderId).orElseThrow();
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("주문 취소 실패 - 존재하지 않는 주문 ID")
    public void t3() throws Exception{
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/orders/{orderId}", 999)
                ).andDo(print());
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404"))
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("주문 취소 실패 - 이미 발송된 경우")
    public void t4() throws Exception{
        ResultActions resultActions = mvc
                .perform(
                        delete("/api/orders/{orderId}", shippedOrderId)
                ).andDo(print());
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("주문완료 상태의 주문만 취소할 수 있습니다."));
    }

    @Test
    @DisplayName("이메일, 주소, 우편번호, 배송예정일이 모두 같으면 같은 주문으로 합산")
    public void t5() throws Exception {
        clock.set(LocalDateTime.of(2026, 6, 9, 13, 0));
        String requestBody = orderRequest("merge@test.com", "서울 종로구 종로 1", "03154");

        ResultActions resultActions = mvc
                .perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("주문이 생성되었습니다."));

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mvc.perform(get("/api/orders").param("email", "merge@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].orderItems[0].quantity").value(2));
    }

    @Test
    @DisplayName("판매중지 상품 주문 생성 실패")
    public void t5_1() throws Exception {
        Product stoppedProduct = productRepository.findById(productId).orElseThrow();
        stoppedProduct.updateSelling(false);
        productRepository.save(stoppedProduct);

        String requestBody = """
                {
                   "email" : "stopped@test.com",
                   "address" : "서울 종로구 종로 1",
                   "zipcode": "03154",
                   "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(productId);

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("판매중지된 상품은 주문할 수 없습니다."));
    }

    @Test
    @DisplayName("이메일, 우편번호, 배송예정일이 같아도 주소가 다르면 다른 주문")
    public void t6_addressDiffers() throws Exception {
        clock.set(LocalDateTime.of(2026, 6, 9, 13, 0));
        String requestBody1 = orderRequest("address-rule@test.com", "서울 종로구 종로 1", "03154");
        String requestBody2 = orderRequest("address-rule@test.com", "서울 종로구 종로 2", "03154");

        ResultActions resultActions = mvc
                .perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("주문이 생성되었습니다."));

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isOk());

        mvc.perform(get("/api/orders").param("email", "address-rule@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("이메일, 주소, 배송예정일이 같아도 우편번호가 다르면 다른 주문")
    public void t6_zipcodeDiffers() throws Exception {
        clock.set(LocalDateTime.of(2026, 6, 9, 13, 0));
        String requestBody1 = orderRequest("zipcode-rule@test.com", "서울 종로구 종로 1", "03154");
        String requestBody2 = orderRequest("zipcode-rule@test.com", "서울 종로구 종로 1", "03155");

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isOk());

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isOk());

        mvc.perform(get("/api/orders").param("email", "zipcode-rule@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("주소, 우편번호, 배송예정일이 같아도 이메일이 다르면 다른 주문")
    public void t6_emailDiffers() throws Exception {
        clock.set(LocalDateTime.of(2026, 6, 9, 13, 0));
        String address = "서울 종로구 종로 1";
        String zipcode = "03154";

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest("email-rule-a@test.com", address, zipcode)))
                .andExpect(status().isOk());

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest("email-rule-b@test.com", address, zipcode)))
                .andExpect(status().isOk());

        mvc.perform(get("/api/orders").param("email", "email-rule-a@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        mvc.perform(get("/api/orders").param("email", "email-rule-b@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("이메일, 주소, 우편번호가 같아도 14시 전후 배송예정일이 다르면 다른 주문")
    public void t7() throws Exception {
        String requestBody = orderRequest("cutoff-rule@test.com", "서울 종로구 종로 1", "03154");

        clock.set(LocalDateTime.of(2026, 6, 9, 14, 0));
        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        clock.set(LocalDateTime.of(2026, 6, 9, 14, 0, 1));
        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mvc.perform(get("/api/orders").param("email", "cutoff-rule@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].shippingDate").value("2026-06-09T00:00:00"))
                .andExpect(jsonPath("$.data[1].shippingDate").value("2026-06-10T00:00:00"));
    }


}
