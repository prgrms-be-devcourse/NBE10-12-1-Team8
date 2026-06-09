package com.backend.domain.product.product.controller;

import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.AdminProductService;
import com.backend.domain.product.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductService productService;
    @Autowired
    private AdminProductService adminProductService;

    @Test
    @DisplayName("사용자 상품조회")
    void t1()throws Exception {
        mvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("사용자 상품조회에서 판매중지 상품 제외")
    void stoppedProductIsHidden() throws Exception {
        Product product = adminProductService.save(
                "판매중지 상품",
                10000,
                "사용자 목록에 노출되지 않는 상품",
                "http://test.com/stopped.jpg"
        );
        adminProductService.updateSalesStatus(product.getId(), false);

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name", not(hasItem("판매중지 상품"))));
    }

}
