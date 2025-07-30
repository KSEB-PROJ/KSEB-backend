package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventTimeInfoResponse {    //해당하는 이벤트의 날짜와 끝시간만 필요해서 dto도 따로 파줬음
    private Long eventId;
    private Long ownerId;
    private String ownerType;
    private String rrule;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    public static EventTimeInfoResponse from(Event event) {
        EventTimeInfoResponse dto = new EventTimeInfoResponse();
        dto.setEventId(event.getId());
        dto.setOwnerId(event.getOwnerId());
        dto.setOwnerType(event.getOwnerType().name());
        dto.setRrule(event.getRrule());
        dto.setStartDatetime(event.getStartDatetime());
        dto.setEndDatetime(event.getEndDatetime());
        return dto;
    }
}
