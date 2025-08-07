package com.kseb.collabtool.domain.admin.dto;

import com.kseb.collabtool.domain.user.entity.Role;
import com.kseb.collabtool.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserAdminResponse {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private LocalDateTime createdAt;

    public static UserAdminResponse from(User user) {
        return UserAdminResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
