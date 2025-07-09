package com.kseb.collabtool.domain.schedule.entity;

import com.kseb.collabtool.domain.events.entity.TaskStatus;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_tasks")
@Data
public class ScheduleTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 할 일 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // 소속 일정

    @Column(nullable = false, length = 255)
    private String title; // 할 일 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee; // 담당자(선택)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus taskStatus; // 상태(TODO/DOING/DONE)

    @Column(name = "due_date")
    private LocalDateTime dueDate; // 마감일

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시각

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 마지막 수정 시각

}
