package com.kseb.collabtool.domain.message.entity;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 메시지 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 소속 채널

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 보낸 사용자

    @Column(columnDefinition = "TEXT")
    private String content; // 텍스트 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_type_id", nullable = false)
    private MessageType messageType; // 메시지 유형(TEXT, IMAGE 등)

    @Column(name = "file_url", length = 512)
    private String fileUrl; // 첨부 파일 URL

    @Column(name = "file_name", length = 255)
    private String fileName; // 첨부 파일명

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 전송 시각

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}