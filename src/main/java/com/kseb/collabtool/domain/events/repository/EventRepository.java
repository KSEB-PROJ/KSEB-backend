package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("SELECT COUNT(e) > 0 FROM Event e " +
            "JOIN EventParticipant p ON e.id = p.event.id " +
            "WHERE p.user.id = :userId " +
            "AND NOT (:newEnd <= e.startDatetime OR :newStart >= e.endDatetime)")
    boolean existsOverlap(@Param("userId") Long userId,
                       @Param("newStart") LocalDateTime newStart,
                       @Param("newEnd") LocalDateTime newEnd);



}

