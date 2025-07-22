package com.kseb.collabtool.domain.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventTaskCreateRequest {
    private String title;
    private Long assigneeId;       // 담당자
    private Long statusId;         // TaskStatus id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDatetime;
}
