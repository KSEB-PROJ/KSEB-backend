package com.kseb.collabtool.domain.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;          // 닉네임
    //password: 별도의 비밀번호 변경 API
    //email: 일반적으로 수정 불가(수정하려면 이메일 인증 등 별도 절차 필요)
}
