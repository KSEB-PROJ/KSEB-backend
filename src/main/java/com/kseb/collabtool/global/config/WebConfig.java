package com.kseb.collabtool.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /profile-images/** URL을 실제 폴더와 연결
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations("file:src/main/resources/static/profile-images/");
        // 운영 서버라면 예시: .addResourceLocations("file:/home/ubuntu/profile-uploads/");
    }
}