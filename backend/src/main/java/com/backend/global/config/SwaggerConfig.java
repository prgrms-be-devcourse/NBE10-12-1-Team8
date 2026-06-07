package com.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🍅 Grids & Circles 카페 메뉴 관리 API")
                        .description("카페 메뉴 관리 서비스 REST API 문서입니다.")
                        .version("v1.0.0"));
    }
}
