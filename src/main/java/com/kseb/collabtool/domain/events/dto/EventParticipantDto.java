package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.ParticipantStatus;
import com.kseb.collabtool.domain.events.entity.EventParticipant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipantDto { //스케쥴 참여자
    private Long userId;
    private String userName;
    private ParticipantStatus status;

    public static EventParticipantDto from(EventParticipant ep) {
        return new EventParticipantDto(
                ep.getUser().getId(),
                ep.getUser().getName(),
                ep.getStatus()
        );
    }
}
