package com.kseb.collabtool.global.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 인증 필터.
 * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하고,
 * 유효성을 검사하여 SecurityContext에 인증 정보를 저장한다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * JWT 토큰의 유효성을 검사하고 인증 정보를 SecurityContext에 저장.
     * 인증이 실패해도 예외를 던지지 않고, SecurityContext를 비운다.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 1. Request Header에서 토큰을 꺼냅니다.
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰이 유효한지 확인합니다.
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아와서 Authentication 객체를 생성합니다.
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // SecurityContext 에 Authentication 객체를 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("JWT 인증 오류: {}", e.getMessage(), e);
            // 에러 발생 시 SecurityContext 비움
            SecurityContextHolder.clearContext();
        }

        // 3. 다음 필터로 요청을 넘깁니다.
        filterChain.doFilter(request, response);
    }
}
