package com.kseb.collabtool.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private Long channelId;         // 방 ID
    private String content;         // 텍스트 메시지
    private Short messageTypeId;    // 메시지 타입 (TEXT, IMAGE 등)
    private String fileUrl;         // 파일 URL (선택)
    private String fileName;        // 파일명 (선택)
}