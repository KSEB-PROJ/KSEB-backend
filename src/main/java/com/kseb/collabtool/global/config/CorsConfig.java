package com.kseb.collabtool.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); //쿠키, 세션 등 인증정보 포함 요청 허용
        config.setAllowedOrigins(List.of(  //허용할 도메인 지정
                "http://localhost:5173"
        ));
        //허용할 HTTP 메서드 지정
        // 여기는 CORS 정책상 어떤 메서드를 허용할지 알려주는 것 뿐이지 OPTIONS를 허용하는게 아님
        // 실제 제로 OPTIONS 메서드로 요청이 들어왔을 때이걸 차단할지, 허용할지를 결정하는 건 SecurityConfig
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        //허용할 HTTP 헤더 지정
        config.setAllowedHeaders(List.of("*"));
        // 응답에서 노출할 헤더 지정
        config.setExposedHeaders(List.of("*"));
        //위 설정을 모든 경로(`/**`)에 적용하도록 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

//https://velog.io/@juhyeon1114/Spring-security%EC%97%90%EC%84%9C-CORS%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0