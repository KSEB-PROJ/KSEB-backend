package com.kseb.collabtool.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //비즈니스 커스텀 예외
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(
            GeneralException ex,
            HttpServletRequest request
    ) {
        Body body = ex.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);

        return super.handleExceptionInternal(
                ex, response, HttpHeaders.EMPTY, body.getHttpStatus(), webRequest
        );
    }

    //권한/인가 실패 (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        Body body = Status.UNAUTHORIZED.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);

        return super.handleExceptionInternal(
                ex, response, HttpHeaders.EMPTY, body.getHttpStatus(), webRequest
        );
    }

    //파라미터/Validation 오류(@Valid)
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        Body body = Status.BAD_REQUEST.getBody();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(fieldName, errorMessage,
                    (existing, newMsg) -> existing + ", " + newMsg);
        });

        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), errors);

        return super.handleExceptionInternal(
                ex, response, headers, Status.BAD_REQUEST.getHttpStatus(), request
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        Body body;

        if (ex instanceof BadCredentialsException) {
            // 아이디/비밀번호 틀림 (로그인 실패)
            body = Status.LOGIN_FAILED.getBody();
        } else {
            // 토큰 없음/만료 등 기타 인증 실패   -> 쓸 일 있을련지 모르겠음
            body = Status.UNAUTHORIZED.getBody();
        }

        ApiResponse<Object> response = ApiResponse.onFailure(
                body.getCode(),
                body.getMessage(),
                null
        );
        return ResponseEntity.status(body.getHttpStatus()).body(response);
    }


    //기타 모든 예외(예상치 못한 서버 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(
            Exception ex,
            WebRequest webRequest
    ) {
        log.error("=== [UNEXPECTED ERROR] ===");
        log.error("Exception: {}", ex.getClass().getName());
        log.error("Message: {}", ex.getMessage(), ex); // 스택트레이스 포함

        Body body = Status.INTERNAL_SERVER_ERROR.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);

        return super.handleExceptionInternal(
                ex, response, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, webRequest
        );
    }
}
