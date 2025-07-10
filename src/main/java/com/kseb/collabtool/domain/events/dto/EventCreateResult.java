package com.kseb.collabtool.domain.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCreateResult {
    private Long eventId;
    private boolean hasOverlap;
}
