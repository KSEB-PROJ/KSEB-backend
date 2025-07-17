package com.kseb.collabtool.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private boolean success;    // 성공 여부
    private String code;        // 에러 코드(성공 시 null)
    private String message;     // 메시지(에러 메시지, 성공 메시지 등)
    private T data;             // 실제 데이터(payload)

    // 성공 응답용
    public static <T> ApiResponse<T> onSuccess(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(null)
                .message(null)
                .data(data)
                .build();
    }

    // 실패(에러) 응답용
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}