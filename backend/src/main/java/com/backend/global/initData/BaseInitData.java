package com.backend.global.initData;

import com.backend.domain.order.order.dto.OrderItemRequest;
import com.backend.domain.order.order.entity.Order;
import com.backend.domain.order.order.repository.OrderRepository;
import com.backend.domain.order.order.service.OrderService;
import com.backend.domain.product.product.entity.Product;
import com.backend.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;

    private final ProductService productService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

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

        productService.create("에티오피아 예가체프", 32000, "꽃향과 산미가 특징인 싱글오리진 원두", "/product-images/ethiopia.jpg");
        productService.create("콜롬비아 수프리모", 15000, "균형 잡힌 바디감과 고소한 풍미의 원두", "/product-images/colombia.jpg");
        productService.create("케냐 AA", 18000, "베리류의 과일향과 밝은 산미가 특징인 원두", "/product-images/kenya.jpg");
        productService.create("브라질 산토스", 13000, "초콜릿과 견과류 향이 풍부한 부드러운 원두", "/product-images/brazil.jpg");
    }

    @Transactional
    public void work2() {
        if (orderService.count() > 0) return;

        List<Product> products = productService.findAll();
        Product p1 = products.get(0); // 에티오피아 예가체프
        Product p2 = products.get(1); // 콜롬비아 수프리모
        Product p3 = products.get(2); // 케냐 AA
        Product p4 = products.get(3); // 브라질 산토스
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        // bean@test.com — 주문 2건 (다른 주소로 유니크 제약 충족)
        orderService.create("bean@test.com", "서울 강남구 테헤란로 123", "06234",
                List.of(
                        new OrderItemRequest(p1.getId(), 2),
                        new OrderItemRequest(p2.getId(), 1)
                )
        );
        orderService.create("bean@test.com", "서울 마포구 홍대로 100", "03920",
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

        // cutoff@test.com — 오늘 14시 이전 주문: 배송일이 오늘로 잡힘
        createOrder("cutoff@test.com", "서울 송파구 올림픽로 300", "05551", today.withHour(9).withMinute(30),
                today,
                List.of(p1, p4),
                List.of(1, 2)
        );
        createOrder("cutoff2@test.com", "서울 용산구 한강대로 405", "04320", today.withHour(13).withMinute(55),
                today,
                List.of(p2),
                List.of(2)
        );

        // late@test.com — 오늘 14시 이후 주문: 배송일이 내일로 잡힘
        createOrder("late@test.com", "경기 성남시 분당구 판교역로 166", "13529", today.withHour(14).withMinute(10),
                today.plusDays(1),
                List.of(p3, p4),
                List.of(1, 1)
        );
        createOrder("late2@test.com", "인천 연수구 센트럴로 123", "22004", today.withHour(18).withMinute(40),
                today.plusDays(1),
                List.of(p1),
                List.of(3)
        );

        // same-key@test.com — 같은 이메일/주소/우편번호이지만 14시 기준 배송일이 달라 복합 유니크키 충돌 없이 저장됨
        createOrder("same-key@test.com", "서울 중구 세종대로 110", "04524", today.withHour(10).withMinute(15),
                today,
                List.of(p1, p2),
                List.of(1, 1)
        );
        createOrder("same-key@test.com", "서울 중구 세종대로 110", "04524", today.withHour(15).withMinute(20),
                today.plusDays(1),
                List.of(p3),
                List.of(2)
        );

        // item-merge@test.com — 같은 주문 안에 같은 상품을 여러 번 담아 order_item 복합 유니크키 병합 동작 확인
        createOrder("item-merge@test.com", "대전 서구 둔산로 100", "35242", today.withHour(11).withMinute(5),
                today,
                List.of(p4, p4, p2),
                List.of(1, 2, 1)
        );
    }

    private void createOrder(String email, String address, String zipcode, LocalDateTime orderAt, LocalDateTime shippingDate,
                             List<Product> products, List<Integer> quantities) {
        Order order = new Order(email, shippingDate, address, zipcode, orderAt);

        for (int i = 0; i < products.size(); i++) {
            order.addUpdateOrderItem(products.get(i), quantities.get(i));
        }

        orderRepository.save(order);
    }
}
