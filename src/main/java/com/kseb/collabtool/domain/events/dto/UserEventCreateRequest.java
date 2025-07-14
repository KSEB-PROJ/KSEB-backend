package com.kseb.collabtool.domain.events.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserEventCreateRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private boolean allDay;
    private String rrule;
    private String themeColor;
}
