package com.kseb.collabtool.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum Status {

    // ==== 공통 ====
    OK(HttpStatus.OK, "common.ok", "성공입니다."),
    CREATED(HttpStatus.CREATED, "common.created", "생성되었습니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "common.bad_request", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "common.unauthorized", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "common.forbidden", "접근이 금지되었습니다."),
    CONFLICT(HttpStatus.CONFLICT, "common.conflict", "이미 존재하는 데이터입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "common.not_found", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "common.server_error", "서버 오류가 발생했습니다."),

    // ==== 유저/멤버 ====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not_found", "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "user.email_already_exists", "이미 등록된 이메일입니다."),
    MEMBER_ALREADY_JOINED(HttpStatus.CONFLICT, "member.already_joined", "이미 그룹에 가입된 사용자입니다."),
    MEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "member.role_not_found", "멤버 역할 정보를 찾을 수 없습니다."),

    // ==== 그룹 ====
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "group.not_found", "그룹을 찾을 수 없습니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "group.invalid_invite_code", "유효하지 않은 초대코드입니다."),

    // ==== 채널 ====
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "channel.not_found", "채널을 찾을 수 없습니다."),
    CHANNEL_TYPE_ALREADY_EXISTS(HttpStatus.CONFLICT, "channel.type_already_exists", "해당 타입의 채널이 이미 존재합니다."),
    CHANNEL_CREATE_ONLY_LEADER(HttpStatus.FORBIDDEN, "channel.create_only_leader", "채널 생성은 그룹 리더만 가능합니다."),
    CHANNEL_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "channel.type_not_found", "채널 타입이 존재하지 않습니다."),
    CHANNEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "channel.already_exists", "이미 존재하는 채널입니다."),

    // ==== 파일 ====
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "file.not_found", "파일이 존재하지 않습니다."),

    // ==== 인증/권한 ====
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "auth.invalid_password", "비밀번호가 올바르지 않습니다."),
    // ==== 공지 ====
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "notice.not_found", "공지사항이 존재하지 않습니다."),
    INVALID_PINNED_UNTIL(HttpStatus.BAD_REQUEST, "notice.invalid_pinned_until", "만료시간이 현재보다 이전일 수 없습니다."),
    // ==== 스케쥴 ====
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "schedule.not_found", "일정이 존재하지 않습니다.");


    // ==== 기타 (필요시 추가) ====
    

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public Body getBody() {
        return Body.builder()
                .code(code)
                .message(message)
                .isSuccess(httpStatus.is2xxSuccessful())
                .httpStatus(httpStatus)
                .build();
    }
}
