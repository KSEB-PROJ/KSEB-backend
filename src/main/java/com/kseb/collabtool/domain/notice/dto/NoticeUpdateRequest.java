package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
public class NoticeUpdateRequest {
    private String content; // 수정할 공지 내용
    private LocalDateTime pinnedUntil;
}
