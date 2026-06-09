package com.backend.domain.product.product.controller;

import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.AdminProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminProductService adminProductService;

    private Long savedProductId;

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전 기준 상품 1개 생성
        Product product = adminProductService.save(
                "테스트 상품",
                10000,
                "테스트 설명",
                "http://test.com/image.jpg"
        );
        savedProductId = product.getId();
    }

    // ──────────────────────────────────────────
    // A-01 ~ A-03 : 조회
    // ──────────────────────────────────────────

    @Test
    @DisplayName("A-01: 관리자 상품 목록 조회 성공")
    void getProducts() throws Exception {
        mvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[*].id", hasItem(savedProductId.intValue())))
                .andExpect(jsonPath("$.data[*].name", hasItem("테스트 상품")))
                .andExpect(jsonPath("$.data[*].selling", hasItem(true)));
    }

    @Test
    @DisplayName("A-02: 상품 상세 조회 성공 - createDate, modifyDate 포함")
    void getProduct() throws Exception {
        mvc.perform(get("/api/admin/products/" + savedProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(savedProductId))
                .andExpect(jsonPath("$.data.name").value("테스트 상품"))
                .andExpect(jsonPath("$.data.price").value(10000))
                .andExpect(jsonPath("$.data.description").value("테스트 설명"))
                .andExpect(jsonPath("$.data.imageUrl").value("http://test.com/image.jpg"))
                .andExpect(jsonPath("$.data.selling").value(true))
                .andExpect(jsonPath("$.data.createDate").exists())
                .andExpect(jsonPath("$.data.modifyDate").exists());
    }

    @Test
    @DisplayName("A-03: 존재하지 않는 상품 조회 시 404")
    void getProduct_notFound() throws Exception {
        mvc.perform(get("/api/admin/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404"))
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다. id=9999"));
    }

    // ──────────────────────────────────────────
    // A-04 ~ A-07 : 등록
    // ──────────────────────────────────────────

    @Test
    @DisplayName("A-04: 상품 등록 성공 - 201 반환")
    void createProduct() throws Exception {
        String requestBody = """
                {
                    "name": "새 상품",
                    "price": 15000,
                    "description": "새 상품 설명",
                    "imageUrl": "http://new.com/image.jpg"
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("상품 등록 성공"))
                .andExpect(jsonPath("$.data.name").value("새 상품"))
                .andExpect(jsonPath("$.data.price").value(15000));
    }

    @Test
    @DisplayName("A-04-1: 상품 등록 성공 - 가격 0 허용")
    void createProduct_zeroPrice() throws Exception {
        String requestBody = """
                {
                    "name": "무료 샘플",
                    "price": 0,
                    "description": "무료 샘플 설명",
                    "imageUrl": "http://new.com/sample.jpg"
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("상품 등록 성공"))
                .andExpect(jsonPath("$.data.name").value("무료 샘플"))
                .andExpect(jsonPath("$.data.price").value(0))
                .andExpect(jsonPath("$.data.description").value("무료 샘플 설명"))
                .andExpect(jsonPath("$.data.imageUrl").value("http://new.com/sample.jpg"));
    }

    @Test
    @DisplayName("A-05: name 빈값으로 등록 시 400 - 상품명은 필수입니다.")
    void createProduct_blankName() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "price": 10000
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value(containsString("상품명은 필수입니다.")));
    }

    @Test
    @DisplayName("A-06: price 음수로 등록 시 400 - 가격은 0 이상이어야 합니다.")
    void createProduct_negativePrice() throws Exception {
        String requestBody = """
                {
                    "name": "테스트 상품",
                    "price": -1,
                    "description": "테스트 설명",
                    "imageUrl": "http://test.com/image.jpg"
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("가격은 0 이상이어야 합니다."));
    }

    @Test
    @DisplayName("A-07: description 빈값으로 등록 시 400 - 상품 설명은 필수입니다.")
    void createProduct_blankDescription() throws Exception {
        String requestBody = """
                {
                    "name": "테스트 상품",
                    "price": 10000,
                    "description": "",
                    "imageUrl": "http://test.com/image.jpg"
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("상품 설명은 필수입니다."));
    }

    @Test
    @DisplayName("A-08: imageUrl 빈값으로 등록 시 400 - 상품 이미지는 필수입니다.")
    void createProduct_blankImageUrl() throws Exception {
        String requestBody = """
                {
                    "name": "테스트 상품",
                    "price": 10000,
                    "description": "테스트 설명",
                    "imageUrl": ""
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("상품 이미지는 필수입니다."));
    }

    @Test
    @DisplayName("A-09: name null로 등록 시 400")
    void createProduct_nullName() throws Exception {
        String requestBody = """
                {
                    "price": 10000
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"));
    }

    @Test
    @DisplayName("A-09-1: name 공백으로 등록 시 400")
    void createProduct_whitespaceName() throws Exception {
        String requestBody = """
                {
                    "name": "   ",
                    "price": 10000,
                    "description": "테스트 설명",
                    "imageUrl": "http://test.com/image.jpg"
                }
                """;

        mvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("상품명은 필수입니다."));
    }

    @Test
    @DisplayName("A-08: 상품 이미지 업로드 성공 - 201 반환")
    void uploadProductImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "coffee.png",
                MediaType.IMAGE_PNG_VALUE,
                "image-content".getBytes()
        );

        mvc.perform(multipart("/api/admin/products/images")
                        .file(image))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("상품 이미지 업로드 성공"))
                .andExpect(jsonPath("$.data.imageUrl").value(startsWith("/uploads/product-images/")))
                .andExpect(jsonPath("$.data.imageUrl").value(org.hamcrest.Matchers.endsWith(".png")));
    }

    @Test
    @DisplayName("A-08-1: 상품 이미지 업로드 실패 - 빈 파일")
    void uploadProductImage_emptyFile() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "empty.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        mvc.perform(multipart("/api/admin/products/images")
                        .file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("업로드할 이미지 파일을 선택해주세요."));
    }

    @Test
    @DisplayName("A-08-2: 상품 이미지 업로드 실패 - 이미지가 아닌 파일")
    void uploadProductImage_notImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "coffee.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not-image".getBytes()
        );

        mvc.perform(multipart("/api/admin/products/images")
                        .file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("jpg, png, webp, gif 이미지만 업로드할 수 있습니다."));
    }

    // ──────────────────────────────────────────
    // A-08 ~ A-10 : 수정
    // ──────────────────────────────────────────

    @Test
    @DisplayName("A-08: 상품 수정 성공")
    void updateProduct() throws Exception {
        String requestBody = """
                {
                    "name": "수정된 상품",
                    "price": 20000,
                    "description": "수정된 설명",
                    "imageUrl": "http://modified.com/image.jpg"
                }
                """;

        mvc.perform(put("/api/admin/products/" + savedProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("상품 수정 성공"))
                .andExpect(jsonPath("$.data.name").value("수정된 상품"))
                .andExpect(jsonPath("$.data.price").value(20000))
                .andExpect(jsonPath("$.data.description").value("수정된 설명"))
                .andExpect(jsonPath("$.data.imageUrl").value("http://modified.com/image.jpg"));
    }

    @Test
    @DisplayName("A-09: 존재하지 않는 상품 수정 시 404")
    void updateProduct_notFound() throws Exception {
        String requestBody = """
                {
                    "name": "수정된 상품",
                    "price": 20000
                }
                """;

        mvc.perform(put("/api/admin/products/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404"));
    }

    @Test
    @DisplayName("A-10: 수정 시 name 빈값이면 400")
    void updateProduct_blankName() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "price": 20000
                }
                """;

        mvc.perform(put("/api/admin/products/" + savedProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"));
    }

    @Test
    @DisplayName("A-10-1: 수정 시 price 음수이면 400")
    void updateProduct_negativePrice() throws Exception {
        String requestBody = """
                {
                    "name": "수정된 상품",
                    "price": -1,
                    "description": "수정된 설명",
                    "imageUrl": "http://modified.com/image.jpg"
                }
                """;

        mvc.perform(put("/api/admin/products/" + savedProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.message").value("가격은 0 이상이어야 합니다."));
    }

    @Test
    @DisplayName("A-10-2: 상품 id 타입이 숫자가 아니면 500")
    void updateProduct_notNumberId() throws Exception {
        String requestBody = """
                {
                    "name": "수정된 상품",
                    "price": 20000
                }
                """;

        mvc.perform(put("/api/admin/products/not-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("500"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."));
    }

    // ──────────────────────────────────────────
    // A-11 ~ A-12 : 판매상태 변경
    // ──────────────────────────────────────────

    @Test
    @DisplayName("A-11: 상품 판매중지 성공")
    void stopSellingProduct() throws Exception {
        String requestBody = """
                {
                    "selling": false
                }
                """;

        mvc.perform(patch("/api/admin/products/" + savedProductId + "/sales-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("상품 판매상태 변경 성공"))
                .andExpect(jsonPath("$.data.id").value(savedProductId))
                .andExpect(jsonPath("$.data.selling").value(false));

        mvc.perform(get("/api/admin/products/" + savedProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selling").value(false));
    }

    @Test
    @DisplayName("A-12: 존재하지 않는 상품 판매상태 변경 시 404")
    void updateSalesStatus_notFound() throws Exception {
        String requestBody = """
                {
                    "selling": false
                }
                """;

        mvc.perform(patch("/api/admin/products/9999/sales-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404"));
    }
}
