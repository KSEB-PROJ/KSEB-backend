package com.kseb.collabtool.global.config;

import com.kseb.collabtool.global.security.JwtAuthenticationFilter;
import com.kseb.collabtool.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // CORS 설정 적용
                .httpBasic(AbstractHttpConfigurer::disable) // Http Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화

                // JWT를 사용하기 때문에 세션을 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Preflight 요청은 모두 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 아래 경로들은 인증 없이 접근 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/ws/**", // WebSocket 접속 경로 추가
                                "/ws/**",
                                "/ws-stomp/**",
                                "/profile-images/**", //정적 프로필 이미지 접근 허용
                                "/chat-files/**"      // 채팅 파일 경로 허용
                        ).permitAll()
                        // /api/admin/** 경로는 ADMIN 역할만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 /api/** 경로는 인증된 모든 사용자 접근 가능
                        .requestMatchers("/api/**").authenticated()
                        // 그 외 모든 요청은 일단 허용 (필요에 따라 조정)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}