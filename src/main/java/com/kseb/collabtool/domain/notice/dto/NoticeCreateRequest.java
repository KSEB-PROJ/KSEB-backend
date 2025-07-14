package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeCreateRequest {
    private Long channelId; // 공지 채널 ID
    private String content; // 공지 내용
}
