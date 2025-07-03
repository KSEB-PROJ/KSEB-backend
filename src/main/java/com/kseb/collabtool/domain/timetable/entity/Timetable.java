package com.kseb.collabtool.domain.timetable.entity;

import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "timetables")
@Data
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 시간표 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_type_id", nullable = false)
    private TimetableType timetableType; // 강의/알바/약속 등

    @Column(nullable = false, length = 255)
    private String title; // 과목명, 아르바이트명, 약속명

    @Column(length = 100)
    private String professor; // 강의 교수명 (LECTURE 전용)

    @Column(length = 255)
    private String location; // 장소

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek; // MON~SUN

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // 시작 시각

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // 종료 시각

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = true; // 반복 여부(1=반복, 0=단발)

    @Column(columnDefinition = "TEXT")
    private String memo; // 상세 메모

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 등록일

}

