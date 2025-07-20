package com.kseb.collabtool.domain.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventTaskDto {
    private String title;
    private Long assigneeId;
    private Long taskStatusId;
    private LocalDateTime dueDatetime;
}
