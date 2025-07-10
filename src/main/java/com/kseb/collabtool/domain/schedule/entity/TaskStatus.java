package com.kseb.collabtool.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "task_statuses")
@Data
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // TINYINT PK

    @Column(nullable = false, unique = true, length = 32)
    private String code; // TODO, DOING, DONE 등
    //1번 todo 2번 doing 3번 done

    @Column(nullable = false, length = 64)
    private String name; // UI 표시명 (할 일, 진행, 완료 등)

    @Column(length = 20)
    private String color; // 상태별 색상 (hex)

}

