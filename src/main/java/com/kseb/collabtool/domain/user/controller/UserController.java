package com.kseb.collabtool.domain.user.controller;

import com.kseb.collabtool.domain.user.dto.PasswordChangeRequest;
import com.kseb.collabtool.domain.user.dto.UserResponse;
import com.kseb.collabtool.domain.user.dto.UserUpdateRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.service.UserService;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 정보(이름) 및 프로필 이미지 수정
     *
     * @RequestPart를 사용해 이름(dto)과 이미지 파일(profileImg)을 한번에 처리
     */
    @PatchMapping(value = "/me", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserResponse> patchUserInfo(
            @RequestPart(value = "dto", required = false) UserUpdateRequest dto,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        UserResponse result = userService.patchUser(user.getId(), dto, profileImg);
        return ResponseEntity.ok(result);
    }

    /**
     * 프로필 이미지 삭제
     */
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<UserResponse> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        UserResponse result = userService.deleteProfileImage(user.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        UserResponse result = userService.getCurrentUser(user.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody PasswordChangeRequest request
    ) {
        User user = currentUser.getUser();
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok().build(); // 성공시 200 OK만
    }
}
