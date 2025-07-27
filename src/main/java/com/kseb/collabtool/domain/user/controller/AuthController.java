package com.kseb.collabtool.domain.user.controller;

import com.kseb.collabtool.domain.user.dto.UserLoginRequest;
import com.kseb.collabtool.domain.user.dto.UserRegisterRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.service.UserService;
import com.kseb.collabtool.global.exception.ApiResponse;
import com.kseb.collabtool.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map; // Map을 사용하기 위해 추가

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성을 위해 주입

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess("회원가입 성공 id=" + user.getId()));
    }


    @PostMapping("/login")
    // HttpServletRequest 파라미터 제거.
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // AuthenticationManager를 통해 인증 수행.
        Authentication auth = authenticationManager.authenticate(token);

        // 인증에 성공하면 SecurityContextHolder에 저장할 필요 없이 바로 토큰을 생성. (Stateless)
        // SecurityContextHolder.getContext().setAuthentication(auth);

        // 세션 생성 및 저장 로직 삭제
        // HttpSession session = httpRequest.getSession(true); // true: 세션이 없으면 새로 생성
        // session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        // httpRequest.getSession(true);

        // 인증 정보를 바탕으로 JWT를 생성.
        String jwt = jwtTokenProvider.createToken(auth);

        // 생성된 토큰을 클라이언트에게 JSON 형태로 반환.
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", jwt)));
    }
}