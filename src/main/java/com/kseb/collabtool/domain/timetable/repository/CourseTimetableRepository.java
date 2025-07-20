package com.kseb.collabtool.domain.timetable.repository;

import com.kseb.collabtool.domain.timetable.entity.CourseTimetable;
import com.kseb.collabtool.domain.timetable.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CourseTimetableRepository extends JpaRepository<CourseTimetable, Long> {

    @Query("SELECT COUNT(t) > 0 FROM CourseTimetable t " +
            "WHERE t.user.id = :userId AND t.semester = :semester AND t.dayOfWeek = :dayOfWeek " +
            "AND ((:startTime < t.endTime) AND (:endTime > t.startTime))")
    boolean existsOverlap(@Param("userId") Long userId,
                          @Param("semester") String semester,
                          @Param("dayOfWeek") DayOfWeek dayOfWeek,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime);

    List<CourseTimetable> findAllByUserIdAndSemester(Long userId, String semester);
    List<CourseTimetable> findAllByUserId(Long userId);
    Optional<CourseTimetable> findByIdAndUserId(Long id, Long userId);
}
