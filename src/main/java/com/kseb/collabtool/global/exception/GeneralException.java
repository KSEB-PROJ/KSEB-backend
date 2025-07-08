package com.kseb.collabtool.global.exception;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException { //커스템 예외
    private final Body body;

    public GeneralException(Status status) {
        super(status.getMessage());
        this.body = status.getBody();
    }

    public GeneralException(Status status, String customMessage) {
        super(customMessage);
        this.body = Body.builder()
                .code(status.getCode())
                .message(customMessage)
                .isSuccess(status.getHttpStatus().is2xxSuccessful())
                .httpStatus(status.getHttpStatus())
                .build();
    }
}
