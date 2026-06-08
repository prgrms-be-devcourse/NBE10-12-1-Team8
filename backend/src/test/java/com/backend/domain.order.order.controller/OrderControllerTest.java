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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    private Long orderdOrderId;
    private Long shippedOrderId;
    private Long productId;

    @BeforeEach
   public void setUp(){

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

    @Test
    @DisplayName("주문 목록 조회")
    void t1() throws Exception{
        ResultActions resultActions = mvc
                .perform(
                        get("/api/orders").param("email", "bean@test.com")

                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("findByEmail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data[0].orderId").exists())
                .andExpect(jsonPath("$.data[0].email").value("bean@test.com"))
                .andExpect(jsonPath("$.data[0].status").exists())
                .andExpect(jsonPath("$.data[0].items").isArray());

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
    @DisplayName("동일 조건 재주문 시 주문 합산")
    public void t5() throws Exception {
        String requestBody = """
                {
                   "email" : "merge@test.com",
                   "address" : "서울 종로구 종로 1",
                   "zipcode": "03154",
                   "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(productId);

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
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("같은 이메일 다른 주소 주문")
    public void t6() throws Exception {
        String requestBody1 = """
                {
                   "email" : "merge@test.com",
                   "address" : "서울 종로구 종로 1",
                   "zipcode": "03154",
                   "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(productId);
        String requestBody2 = """
                {
                   "email" : "merge@test.com",
                   "address" : "서울 종로구 종로 2",
                   "zipcode": "03155",
                   "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(productId);

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

        mvc.perform(get("/api/orders").param("email", "merge@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }


}