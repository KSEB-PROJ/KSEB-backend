package com.kseb.collabtool.domain.notice.entity;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.message.entity.Message;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.groups.entity.Group;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Data
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공지 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 소속 그룹

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 공지 채널 (NOTICE 채널)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_message_id")
    private Message sourceMessage; // 원본 메시지(채팅을 끌어온 공지), null 허용

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 공지 내용

    @Column(name = "pinned_until")
    private LocalDateTime pinnedUntil; // 고정 만료일

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

}
