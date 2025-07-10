package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

    //유저 스케쥴 중복 확인
    @Query("SELECT COUNT(e) > 0 FROM Event e " +
            "JOIN EventParticipant p ON e.id = p.event.id " +
            "WHERE p.user.id = :userId " +
            "AND NOT (:newEnd <= e.startDatetime OR :newStart >= e.endDatetime)")
    boolean existsOverlap(@Param("userId") Long userId,
                       @Param("newStart") LocalDateTime newStart,
                       @Param("newEnd") LocalDateTime newEnd);

    //그룹 스케쥴 중복 확인
    @Query("SELECT COUNT(e) > 0 FROM Event e " +
            "WHERE e.ownerType = 'GROUP' AND e.ownerId = :groupId " +
            "AND NOT (:newEnd <= e.startDatetime OR :newStart >= e.endDatetime)")
    boolean existsGroupOverlap(@Param("groupId") Long groupId,
                               @Param("newStart") LocalDateTime newStart,
                               @Param("newEnd") LocalDateTime newEnd);


    List<Event> findAllByGroupEventId(Long groupEventId);

    List<Event> findByOwnerTypeAndOwnerId(OwnerType ownerType, Long ownerId);
}

