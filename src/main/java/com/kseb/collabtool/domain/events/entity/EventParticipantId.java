package com.kseb.collabtool.domain.events.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventParticipantId implements Serializable { //복합키 설정
    private Long eventId;
    private Long userId;
}
