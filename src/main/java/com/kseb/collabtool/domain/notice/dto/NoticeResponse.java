package com.kseb.collabtool.domain.notice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {
    private Long id;
    private Long groupId;
    private Long channelId;
    private Long userId;
    private String content;
    private Long sourceMessageId;
    private LocalDateTime pinnedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
