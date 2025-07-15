package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.global.validation.ValidRRule;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupEventCreateRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private boolean allDay;
    @ValidRRule
    private String rrule;
    private String themeColor;
}
