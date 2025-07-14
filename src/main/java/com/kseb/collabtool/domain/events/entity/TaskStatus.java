package com.kseb.collabtool.domain.events.entity;

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

    @Column(nullable = false, length = 64)
    private String name; // UI 표시명 (할 일, 진행, 완료 등)

    @Column(length = 20)
    private String color; // 상태별 색상 (hex)

    /* 기본 셋팅값
    INSERT INTO task_statuses (code, name, color)
    VALUES
  ('TODO', '할 일', '#FF0000'),   -- 빨강
  ('DOING', '진행', '#FFA500'),   -- 주황
  ('DONE', '완료', '#008000');    -- 초록

     */
}

