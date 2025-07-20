package com.kseb.collabtool.domain.events.repository;

import com.kseb.collabtool.domain.events.entity.EventParticipant;
import com.kseb.collabtool.domain.events.entity.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
}
