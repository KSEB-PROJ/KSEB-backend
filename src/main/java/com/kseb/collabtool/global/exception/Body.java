package com.kseb.collabtool.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class Body {
    private String code;
    private String message;
    private boolean isSuccess;
    private HttpStatus httpStatus;
}
