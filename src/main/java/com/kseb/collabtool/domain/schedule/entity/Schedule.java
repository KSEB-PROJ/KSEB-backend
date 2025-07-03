package com.kseb.collabtool.domain.schedule.entity;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일정 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 소속 캘린더 채널

    @Column(nullable = false, length = 255)
    private String title; // 일정 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    @Column(length = 255)
    private String location; // 장소 (회의실, Zoom 등)

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime; // 시작 시각

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime; // 종료 시각

    @Column(name = "all_day", nullable = false)
    private Boolean allDay = false; // 종일 여부(1=종일, 0=아님)

    @Column(length = 255)
    private String rrule; // 반복 규칙(RFC 5545 RRULE)

    @Column(length = 20)
    private String color; // 표시 색상(hex 등)

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false; // 취소/숨김 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // 일정 생성자

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 마지막 수정일

}
