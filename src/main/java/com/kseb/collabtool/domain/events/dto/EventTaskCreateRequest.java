package com.kseb.collabtool.domain.events.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventTaskCreateRequest {
    private String title;
    private Long assigneeId;       // 담당자
    private Long statusId;         // TaskStatus id
    private LocalDateTime dueDatetime;
}
