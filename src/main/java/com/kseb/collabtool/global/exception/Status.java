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

    // ===로그인 부분===
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "auth.login_failed", "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "user.password_mismatch", "현재 비밀번호가 일치하지 않습니다."),

    // ==== 유저/멤버 ====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not_found", "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "user.email_already_exists", "이미 등록된 이메일입니다."),
    MEMBER_ALREADY_JOINED(HttpStatus.CONFLICT, "member.already_joined", "이미 그룹에 가입된 사용자입니다."),
    MEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "member.role_not_found", "멤버 역할 정보를 찾을 수 없습니다."),


    //=== 파일 ===
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "file.upload_failed", "파일 저장에 실패했습니다."),
    // ==== 그룹 ====
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "group.not_found", "그룹을 찾을 수 없습니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "group.invalid_invite_code", "유효하지 않은 초대코드입니다."),
    GROUP_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "group.delete_forbidden", "그룹 삭제 권한이 없습니다."),

    // ==== 채널 ====
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "channel.not_found", "채널을 찾을 수 없습니다."),
    CHANNEL_TYPE_ALREADY_EXISTS(HttpStatus.CONFLICT, "channel.type_already_exists", "해당 타입의 채널이 이미 존재합니다."),
    CHANNEL_CREATE_ONLY_LEADER(HttpStatus.FORBIDDEN, "channel.create_only_leader", "채널 생성은 그룹 리더만 가능합니다."),
    CHANNEL_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "channel.type_not_found", "채널 타입이 존재하지 않습니다."),
    CHANNEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "channel.already_exists", "이미 존재하는 채널입니다."),
    CHANNEL_UPDATE_ONLY_LEADER(HttpStatus.FORBIDDEN, "channel.update_only_leader", "채널 수정은 그룹 리더만 가능합니다."),
    CHANNEL_DELETE_ONLY_LEADER(HttpStatus.FORBIDDEN, "channel.delete_only_leader", "채널 삭제는 그룹 리더만 가능합니다."),
    CHANNEL_SYSTEM_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "channel.system_delete_forbidden", "시스템 채널(공지/캘린더)은 삭제할 수 없습니다."),

    // ==== 이벤트 ====
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "event.not_found", "이벤트를 찾을 수 없습니다."),
    NO_AUTHORITY(HttpStatus.FORBIDDEN, "event.no_authority", "이벤트에 대한 권한이 없습니다."),
    TASK_NOT_FOUND(HttpStatus.FORBIDDEN, "task.not_found", "할 일을 찾을 수 없습니다."),

    // ==== 파일 ====
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "file.not_found", "파일이 존재하지 않습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "file.delete_failed", "파일 삭제에 실패했습니다."),
    FILE_NOT_SAVE(HttpStatus.NOT_FOUND, "file.not_save", "파일을 저장할수  없습니다."),

    // ==== 인증/권한 ====
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "auth.invalid_password", "비밀번호가 올바르지 않습니다."),
    // ==== 공지 ====
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "notice.not_found", "공지사항이 존재하지 않습니다."),
    INVALID_PINNED_UNTIL(HttpStatus.BAD_REQUEST, "notice.invalid_pinned_until", "만료시간이 현재보다 이전일 수 없습니다."),
    NOTICE_PROMOTE_ONLY_SELF(HttpStatus.FORBIDDEN, "notice.promote_only_self", "본인이 작성한 메시지만 공지로 승격할 수 있습니다."),
    // ==== 스케쥴 ====
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "schedule.not_found", "일정이 존재하지 않습니다."),


    // ==== 강의 시간표 ====
    TIMETABLE_OVERLAP(HttpStatus.CONFLICT, "timetable.overlap", "겹치는 강의 시간표가 이미 존재합니다."),
    TIMETABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "timetable.not_found", "존재하지 않는 강의입니다."),
    INVALID_SEMESTER_FORMAT(HttpStatus.BAD_REQUEST, "timetable.invalid_semester_format", "학기 형식 오류 "),
    UNSUPPORTED_SEMESTER_CODE(HttpStatus.BAD_REQUEST, "timetable.unsupported_semester_code", "지원하지 않는 학기 코드 ");
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
