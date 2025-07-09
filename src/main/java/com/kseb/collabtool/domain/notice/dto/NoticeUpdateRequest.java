package com.kseb.collabtool.domain.notice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeUpdateRequest {
    private String content; // 수정할 공지 내용
}
