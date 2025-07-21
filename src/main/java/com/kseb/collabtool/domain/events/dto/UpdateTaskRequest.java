package com.kseb.collabtool.domain.events.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest { //Task 변경
    private String title;           // 제목
    private Long assigneeId;        // 담당자
    private Short statusId;         // 상태
    private LocalDateTime dueDatetime; // 마감일
    // 필요하면 다른 것도 바꿔도 됨
}
