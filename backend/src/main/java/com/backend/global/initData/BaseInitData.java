package com.backend.global.initData;

import com.backend.domain.order.order.service.OrderService;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;

    private final ProductService productService;
    private final OrderService orderService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work2();
        };
    }

    @Transactional
    public void work1() {
        if (productService.count() > 0) return;

        productService.create("에티오피아 예가체프", 32000, "꽃향과 산미가 특징인 싱글오리진 원두", "https://example.com/ethiopia.jpg");
        productService.create("콜롬비아 수프리모", 15000, "균형 잡힌 바디감과 고소한 풍미의 원두", "https://example.com/colombia.jpg");
        productService.create("케냐 AA", 18000, "베리류의 과일향과 밝은 산미가 특징인 원두", "https://example.com/kenya.jpg");
        productService.create("브라질 산토스", 13000, "초콜릿과 견과류 향이 풍부한 부드러운 원두", "https://example.com/brazil.jpg");
    }

    @Transactional
    public void work2() {
        if (orderService.count() > 0) return;

        List<Product> products = productService.findAll();
        Product p1 = products.get(0); // 에티오피아 예가체프
        Product p2 = products.get(1); // 콜롬비아 수프리모
        Product p3 = products.get(2); // 케냐 AA
        Product p4 = products.get(3); // 브라질 산토스

        // bean@test.com — 주문 2건 (다음날 배송 배치 묶음 테스트용)
        orderService.create("bean@test.com", "서울 강남구 테헤란로 123", "06234",
                List.of(
                        new OrderItemRequest(p1.getId(), 2),
                        new OrderItemRequest(p2.getId(), 1)
                )
        );
        orderService.create("bean@test.com", "서울 강남구 테헤란로 123", "06234",
                List.of(
                        new OrderItemRequest(p3.getId(), 1)
                )
        );

        // other@test.com — 주문 1건 (다른 이메일 구분 테스트용)
        orderService.create("other@test.com", "부산 해운대구 해운대로 456", "48094",
                List.of(
                        new OrderItemRequest(p4.getId(), 3)
                )
        );
    }
}
