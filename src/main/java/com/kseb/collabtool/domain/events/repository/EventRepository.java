package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kseb.collabtool.domain.timetable.entity.DayOfWeek;
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


    List<Event> findByOwnerTypeAndOwnerId(OwnerType ownerType, Long ownerId);


    // 유저 전체 일정 조회 (개인 + 소속 그룹 일정)
    @Query(value = """
    SELECT * FROM events e
    WHERE (e.owner_type = 'USER' AND e.owner_id = :userId)
       OR (e.owner_type = 'GROUP' AND e.owner_id IN (
             SELECT gm.group_id FROM group_members gm WHERE gm.user_id = :userId
         ))
    """, nativeQuery = true)
    List<Event> findAllEventsForUser(@Param("userId") Long userId);

    //그룹 삭제할 때 같이 날려버림
    @Modifying
    @Transactional
    @Query("DELETE FROM Event e WHERE e.ownerType = :ownerType AND e.ownerId = :ownerId")
    void deleteByOwnerTypeAndOwnerId(@Param("ownerType") OwnerType ownerType, @Param("ownerId") Long ownerId);


        @Query("SELECT e FROM Event e WHERE e.ownerId = :userId AND e.ownerType = :ownerType AND e.title = :title AND e.courseCode = :courseCode AND e.semester = :semester AND e.dayOfWeek = :dayOfWeek")
        Event findCourseEvent(@Param("userId") Long userId,
                              @Param("ownerType") OwnerType ownerType,
                              @Param("title") String title,
                              @Param("courseCode") String courseCode,
                              @Param("semester") String semester,
                              @Param("dayOfWeek") DayOfWeek dayOfWeek);

        @Modifying
        @Transactional
        @Query("DELETE FROM Event e WHERE e.ownerId = :userId AND e.ownerType = :ownerType AND e.title = :title AND e.courseCode = :courseCode AND e.semester = :semester AND e.dayOfWeek = :dayOfWeek")
        void deleteCourseEvent(@Param("userId") Long userId,
                               @Param("ownerType") OwnerType ownerType,
                               @Param("title") String title,
                               @Param("courseCode") String courseCode,
                               @Param("semester") String semester,
                               @Param("dayOfWeek") DayOfWeek dayOfWeek);


    //모든 그룹원에 대해 해당 기간 내에 있는 모든 개인일정 추출
    @Query("SELECT e FROM Event e WHERE e.ownerType = :ownerType AND e.ownerId IN :userIds AND NOT (e.endDatetime <= :from OR e.startDatetime >= :to)")
    List<Event> findAllPersonalEventsForUsers(@Param("ownerType") OwnerType ownerType,
                                              @Param("userIds") List<Long> userIds,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    //기간내에 모든 그룹 일정 추출
    @Query("SELECT e FROM Event e WHERE e.ownerType = :ownerType AND e.ownerId = :groupId AND NOT (e.endDatetime <= :from OR e.startDatetime >= :to)")
    List<Event> findAllGroupEvents(@Param("ownerType") OwnerType ownerType,
                                   @Param("groupId") Long groupId,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);
}


