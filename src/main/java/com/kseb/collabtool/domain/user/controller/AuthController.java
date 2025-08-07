package com.kseb.collabtool.domain.user.controller;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.service.ActivityLogService;
import com.kseb.collabtool.domain.user.dto.UserLoginRequest;
import com.kseb.collabtool.domain.user.dto.UserRegisterRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.service.UserService;
import com.kseb.collabtool.global.exception.ApiResponse;
import com.kseb.collabtool.global.security.CustomUserDetails;
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

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityLogService activityLogService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getName());
        activityLogService.saveLog(user, ActionType.USER_REGISTER, user.getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("회원가입 성공 id=" + user.getId()));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication auth = authenticationManager.authenticate(token);

        String jwt = jwtTokenProvider.createToken(auth);

        // 로그 기록
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        activityLogService.saveLog(userDetails.getUser(), ActionType.USER_LOGIN, userDetails.getUser().getId());

        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", jwt)));
    }
}
