package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
public class NoticeCreateRequest {
    private Long channelId; // 공지 채널 ID
    private String content; // 공지 내용
    private LocalDateTime pinnedUntil;// 프론트에서 날짜 받아옴
}
