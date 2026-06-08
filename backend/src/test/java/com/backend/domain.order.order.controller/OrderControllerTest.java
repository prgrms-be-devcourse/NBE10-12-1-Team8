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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @BeforeEach
   public void setUp(){

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
                .andExpect(jsonPath("$.data").isArray());

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
        int id = orderdOrderId.intValue();
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


}