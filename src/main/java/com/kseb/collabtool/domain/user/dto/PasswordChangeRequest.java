package com.kseb.collabtool.domain.user.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String currentPassword; // 현재 비밀번호
    private String newPassword;     // 새 비밀번호
}
