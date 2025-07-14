package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticePinRequest {
    private LocalDateTime pinnedUntil; // 고정 해제일시 (null이면 고정 해제)
}
