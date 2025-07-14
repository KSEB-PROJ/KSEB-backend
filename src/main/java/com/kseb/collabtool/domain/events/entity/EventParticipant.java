package com.kseb.collabtool.domain.events.entity;

import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipant {

    @EmbeddedId
    private EventParticipantId id;

    @MapsId("eventId") // 내가 맵핑하는 이 필드가, 복합키 클래스(EmbeddedId)에 있는 eventId와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ParticipantStatus status;

}
