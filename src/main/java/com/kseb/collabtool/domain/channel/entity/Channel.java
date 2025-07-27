package com.kseb.collabtool.domain.channel.entity;

import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.notice.entity.Notice;
import com.kseb.collabtool.domain.message.entity.Message;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채널 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 소속 그룹

    @Column(nullable = false, length = 100)
    private String name; // 채널명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_type_id", nullable = false)
    private ChannelType channelType; // CHAT / CALENDAR / NOTICE

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false; // 1=시스템 보호 채널(공지 등)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일시

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

}
