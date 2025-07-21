package com.kseb.collabtool.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.chat-file-url-prefix}")
    private String chatFileUrlPrefix;

    @Value("${file.chat-file-folder}")
    private String chatFileFolder;

    @Value("${file.profile-image-url-prefix}")
    private String profileImageUrlPrefix;

    @Value("${file.profile-image-folder}")
    private String profileImageFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로필 이미지 URL과 실제 폴더 연결
        registry.addResourceHandler(profileImageUrlPrefix + "**")
                .addResourceLocations("file:" + profileImageFolder);

        // 채팅 파일 URL과 실제 폴더 연결
        registry.addResourceHandler(chatFileUrlPrefix + "**")
                .addResourceLocations("file:" + chatFileFolder);
    }
    // 운영 서버라면 예시: .addResourceLocations("file:/home/ubuntu/profile-uploads/");
}