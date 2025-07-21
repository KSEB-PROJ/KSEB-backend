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

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> patchUserInfo(
            @RequestBody UserUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user=currentUser.getUser();
        UserResponse result = userService.patchUser(user.getId(), dto);
        return ResponseEntity.ok(result);
    }


    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> patchProfileImage(
            @RequestPart("profileImg") MultipartFile profileImg,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user=currentUser.getUser();
        UserResponse result = userService.updateProfileImage(user.getId(), profileImg);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me/profile-image")
    public ResponseEntity<UserResponse> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user=currentUser.getUser();
        UserResponse result = userService.deleteProfileImage(user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user=currentUser.getUser();
        UserResponse result = userService.getCurrentUser(user.getId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody PasswordChangeRequest request
    ) {
        User user=currentUser.getUser();
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok().build(); // 성공시 200 OK만
    }
}
