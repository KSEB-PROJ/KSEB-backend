package com.kseb.collabtool.domain.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kseb.collabtool.global.validation.ValidRRule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserEventCreateRequest {
    private String title;
    private String description;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDatetime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDatetime;
    private boolean allDay;
    @ValidRRule
    private String rrule;
    private String themeColor;
}
