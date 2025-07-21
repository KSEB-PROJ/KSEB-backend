package com.kseb.collabtool.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private Long id;                // 메시지 PK
    private Long channelId;         // 방 ID
    private Long userId;            // 작성자 ID
    private String userName;        // 작성자 이름 (User 엔티티에 필드가 있다면)
    private String profileImgUrl;   // 작성자 프로필 이미지 URL 추가
    private String content;         // 텍스트
    private String messageType;     // 메시지 타입 코드 (TEXT, IMAGE 등)
    private String fileUrl;         // 파일 URL
    private String fileName;        // 파일명
    private Boolean isMine;         // 내가 보낸 메시지 여부
    private LocalDateTime createdAt;// 생성 시각
}