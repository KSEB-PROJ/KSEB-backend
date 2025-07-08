package com.kseb.collabtool.domain.user.controller;

import com.kseb.collabtool.domain.user.dto.UserLoginRequest;
import com.kseb.collabtool.domain.user.dto.UserRegisterRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.service.UserService;
import com.kseb.collabtool.global.exception.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess("회원가입 성공 id=" + user.getId()));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(ApiResponse.onSuccess("로그인 성공"));
    }
}
