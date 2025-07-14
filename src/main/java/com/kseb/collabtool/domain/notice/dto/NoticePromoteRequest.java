package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticePromoteRequest {
    private LocalDateTime pinnedUntil; // 공지 고정 만료일 (null이면 일반 공지)
}
