package com.kseb.collabtool.domain.notice.dto;

import com.kseb.collabtool.domain.notice.entity.Notice;
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
    private String userName;
    private Long sourceMessageId;
    private LocalDateTime pinnedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public static NoticeResponse fromEntity(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .groupId(notice.getGroup() != null ? notice.getGroup().getId() : null)
                .channelId(notice.getChannel() != null ? notice.getChannel().getId() : null)
                .userId(notice.getUser() != null ? notice.getUser().getId() : null)
                .userName(notice.getUser() != null ? notice.getUser().getName() : null) // ★
                .content(notice.getContent())
                .sourceMessageId(notice.getSourceMessage() != null ? notice.getSourceMessage().getId() : null)
                .pinnedUntil(notice.getPinnedUntil())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
