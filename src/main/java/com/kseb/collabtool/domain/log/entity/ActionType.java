package com.kseb.collabtool.domain.log.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    // User related actions
    USER_LOGIN("사용자 로그인"),
    USER_LOGOUT("사용자 로그아웃"),
    USER_REGISTER("사용자 회원가입"),
    USER_UPDATE_INFO("사용자 정보 수정"),
    USER_DELETE("사용자 탈퇴"),

    // Group related actions
    GROUP_CREATE("그룹 생성"),
    GROUP_DELETE("그룹 삭제"),
    GROUP_INVITE_USER("사용자 그룹 초대"),
    GROUP_JOIN_USER("사용자 그룹 참가"),
    GROUP_LEAVE_USER("사용자 그룹 탈퇴"),

    // Channel related actions
    CHANNEL_CREATE("채널 생성"),
    CHANNEL_DELETE("채널 삭제"),

    // Message related actions
    MESSAGE_SEND("메시지 전송"),
    MESSAGE_DELETE("메시지 삭제"),

    // Notice related actions
    NOTICE_CREATE("공지 생성"),
    NOTICE_UPDATE("공지 수정"),
    NOTICE_DELETE("공지 삭제"),

    // Event related actions
    EVENT_CREATE("일정 생성"),
    EVENT_UPDATE("일정 수정"),
    EVENT_DELETE("일정 삭제"),

    // Admin related actions
    ADMIN_CHANGE_USER_ROLE("관리자 사용자 역할 변경"),
    ADMIN_DELETE_USER("관리자 사용자 강제 탈퇴"),
    ADMIN_DELETE_GROUP("관리자 그룹 강제 삭제");


    private final String description;
}
