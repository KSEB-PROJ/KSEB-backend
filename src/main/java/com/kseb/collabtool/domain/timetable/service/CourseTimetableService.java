package com.kseb.collabtool.domain.timetable.service;

import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import com.kseb.collabtool.domain.events.repository.EventRepository;
import com.kseb.collabtool.domain.timetable.dto.CourseTimetableDto;
import com.kseb.collabtool.domain.timetable.entity.CourseTimetable;
import com.kseb.collabtool.domain.timetable.entity.DayOfWeek;
import com.kseb.collabtool.domain.timetable.repository.CourseTimetableRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import lombok.RequiredArgsConstructor;
import com.kseb.collabtool.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @Service
 * @description 대학 시간표(CourseTimetable) 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * - 주요 기능: 강의 생성, 조회, 수정, 삭제
 * - 특징: 시간표 데이터가 변경될 때마다 개인 스케줄(Event) 데이터와 자동으로 연동.
 */
@Service
@RequiredArgsConstructor
public class CourseTimetableService {

    //--- 의존성 주입 ---//
    /**
     * @description CourseTimetable 엔티티에 대한 데이터베이스 작업을 위한 리포지토리.
     */
    private final CourseTimetableRepository courseTimetableRepository;
    /**
     * @description Event(개인/그룹 스케줄) 엔티티에 대한 데이터베이스 작업을 위한 리포지토리
     * 시간표 데이터를 개인 스케줄과 연동하기 위해 사용.
     */
    private final EventRepository eventRepository;

    /**
     * @description 학기 코드(S1, SU, S2, WI)에 따른 실제 학기 시작일과 종료일을 정의한 상수 맵.
     * RRULE(반복 규칙)의 종료일을 계산하는 데 사용.
     */
    private static final Map<String, LocalDate[]> SEMESTER_DATES = Map.of(
            "S1", new LocalDate[] { LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 21) },
            "SU", new LocalDate[] { LocalDate.of(2025, 6, 24), LocalDate.of(2025, 8, 12) },
            "S2", new LocalDate[] { LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 21) },
            "WI", new LocalDate[] { LocalDate.of(2025, 12, 24), LocalDate.of(2026, 2, 15) }
    );

    /**
     * @description 특정 사용자의 모든 강의 또는 특정 학기 강의 목록을 조회.
     * @param userId 조회할 사용자의 ID
     * @param semester 특정 학기 코드 (예: "2025-S1"). 없으면 모든 학기 강의를 조회.
     * @return CourseTimetableDto 리스트
     */
    @Transactional(readOnly = true)
    public List<CourseTimetableDto> getAll(Long userId, String semester) {
        if (semester == null || semester.isEmpty()) {
            return courseTimetableRepository.findAllByUserId(userId)
                    .stream().map(CourseTimetableDto::fromEntity).toList();
        }
        return courseTimetableRepository.findAllByUserIdAndSemester(userId, semester)
                .stream().map(CourseTimetableDto::fromEntity).toList();
    }

    /**
     * @description 새로운 강의를 시간표에 등록하고, 해당 내용을 개인 스케줄(Event)에도 복사하여 생성.
     * @param dto 프론트엔드에서 받은 강의 정보 DTO
     * @param userId 강의를 등록하는 사용자의 ID
     * @return 생성된 강의 정보 DTO
     * @throws GeneralException 겹치는 시간대의 강의가 이미 존재할 경우 발생
     */
    @Transactional
    public CourseTimetableDto create(CourseTimetableDto dto, Long userId) {
        // 1. 시간 겹침 검사: 동일 사용자의 동일 학기에 겹치는 시간의 강의가 있는지 확인.
        boolean exists = courseTimetableRepository.existsOverlap(
                userId, dto.getSemester(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        if (exists) {
            throw new GeneralException(Status.TIMETABLE_OVERLAP);
        }

        // 2. 학기 정보로 RRULE 생성
        LocalDate[] period = getSemesterPeriod(dto.getSemester());
        String rrule = generateRrule(dto.getDayOfWeek(), period[1]);

        // 3. CourseTimetable 엔티티를 생성하고 DB에 저장.
        CourseTimetable saved = courseTimetableRepository.save(
                CourseTimetable.builder()
                        .user(User.builder().id(userId).build())
                        .courseCode(dto.getCourseCode())
                        .courseName(dto.getCourseName())
                        .professor(dto.getProfessor())
                        .semester(dto.getSemester())
                        .dayOfWeek(dto.getDayOfWeek())
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .location(dto.getLocation())
                        .rrule(rrule)
                        .themeColor(dto.getThemeColor())
                        .build());

        // 4. Event 엔티티 생성, DB에 저장하여 시간표와 연동.
        User user = User.builder().id(userId).build();
        eventRepository.save(Event.builder()
                .ownerType(OwnerType.USER)
                .ownerId(userId)
                .title(saved.getCourseName())
                .courseCode(saved.getCourseCode())
                .professor(saved.getProfessor())
                .semester(saved.getSemester())
                .dayOfWeek(saved.getDayOfWeek())
                .startDatetime(combineDateTime(saved.getSemester(), saved.getDayOfWeek(), saved.getStartTime()))
                .endDatetime(combineDateTime(saved.getSemester(), saved.getDayOfWeek(), saved.getEndTime()))
                .location(saved.getLocation())
                .allDay(false)
                .rrule(rrule)
                .themeColor(saved.getThemeColor())
                .createdBy(user)
                .updatedBy(user)
                .build());

        return CourseTimetableDto.fromEntity(saved);
    }

    /**
     * @description 단일 강의 시간표 정보를 조회.
     * @param id 조회할 강의의 ID
     * @param userId 현재 로그인한 사용자의 ID (본인 강의만 조회 가능)
     * @return CourseTimetableDto
     */
    @Transactional(readOnly = true)
    public CourseTimetableDto get(Long id, Long userId) {
        CourseTimetable entity = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));
        return CourseTimetableDto.fromEntity(entity);
    }

    /**
     * @description 기존 강의 정보를 수정하고, 연동된 개인 스케줄(Event) 정보도 함께 업데이트
     * @param id 수정할 강의의 ID
     * @param dto 프론트엔드에서 받은 수정할 강의 정보 DTO
     * @param userId 현재 로그인한 사용자의 ID
     * @return 수정된 강의 정보 DTO
     * @throws GeneralException 수정하려는 시간이 다른 강의와 겹칠 경우 발생
     */
    @Transactional
    public CourseTimetableDto patch(Long id, CourseTimetableDto dto, Long userId) {
        // 1. 엔티티를 조회.
        CourseTimetable entity = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));

        //  연동된 Event를 찾기 위해 수정 전의 원본 데이터를 기록.
        String oldCourseName = entity.getCourseName();
        String oldCourseCode = entity.getCourseCode();
        String oldSemester = entity.getSemester();
        DayOfWeek oldDayOfWeek = entity.getDayOfWeek();

        // 2. 시간 정보가 변경되었는지 확인.
        boolean isTimeChanged = !entity.getSemester().equals(dto.getSemester()) ||
                !entity.getDayOfWeek().equals(dto.getDayOfWeek()) ||
                !entity.getStartTime().equals(dto.getStartTime()) ||
                !entity.getEndTime().equals(dto.getEndTime());

        // 3. 시간이 변경되면, 다른 강의와 겹치는지 검사.
        if (isTimeChanged) {
            boolean overlap = courseTimetableRepository.existsOverlap(
                    userId, dto.getSemester(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
            if(overlap) throw new GeneralException(Status.TIMETABLE_OVERLAP);
        }

        // 4. 엔티티의 값 업데이트.
        if (dto.getCourseCode() != null) entity.setCourseCode(dto.getCourseCode());
        if (dto.getCourseName() != null) entity.setCourseName(dto.getCourseName());
        if (dto.getProfessor() != null) entity.setProfessor(dto.getProfessor());
        if (dto.getSemester() != null) entity.setSemester(dto.getSemester());
        if (dto.getDayOfWeek() != null) entity.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartTime() != null) entity.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) entity.setEndTime(dto.getEndTime());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getThemeColor() != null) entity.setThemeColor(dto.getThemeColor());

        // 5. RRULE 다시 계산.
        LocalDate[] period = getSemesterPeriod(entity.getSemester());
        String rrule = generateRrule(entity.getDayOfWeek(), period[1]);
        entity.setRrule(rrule);

        // 6. Event를 찾아서 함께 수정.
        Event event = eventRepository.findCourseEvent(
                userId, OwnerType.USER, oldCourseName, oldCourseCode, oldSemester, oldDayOfWeek
        );
        if (event != null) {
            event.setTitle(entity.getCourseName());
            event.setCourseCode(entity.getCourseCode());
            event.setProfessor(entity.getProfessor());
            event.setSemester(entity.getSemester());
            event.setDayOfWeek(entity.getDayOfWeek());
            event.setStartDatetime(combineDateTime(entity.getSemester(), entity.getDayOfWeek(), entity.getStartTime()));
            event.setEndDatetime(combineDateTime(entity.getSemester(), entity.getDayOfWeek(), entity.getEndTime()));
            event.setLocation(entity.getLocation());
            event.setRrule(rrule);
            event.setThemeColor(entity.getThemeColor());
            event.setUpdatedBy(User.builder().id(userId).build());
        }

        return CourseTimetableDto.fromEntity(entity);
    }

    /**
     * @description 강의 정보를 삭제하고, 연동된 개인 event 삭제.
     * @param id 삭제할 강의의 ID
     * @param userId 현재 로그인한 사용자의 ID
     */
    @Transactional
    public void delete(Long id, Long userId) {
        CourseTimetable timetable = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));

        // 1. 연동된 Event를 먼저 삭제
        eventRepository.deleteCourseEvent(
                userId,
                OwnerType.USER,
                timetable.getCourseName(),
                timetable.getCourseCode(),
                timetable.getSemester(),
                timetable.getDayOfWeek()
        );

        // 2. CourseTimetable 삭제
        courseTimetableRepository.delete(timetable);
    }

    //--- Private Helper Methods ---//

    /**
     * @description 학기 코드(예: "2025-S1")를 받아 실제 시작일과 종료일 배열을 반환.
     */
    private LocalDate[] getSemesterPeriod(String semester) {
        String[] parts = semester.split("-");
        if (parts.length != 2) throw new GeneralException(Status.INVALID_SEMESTER_FORMAT);
        LocalDate[] arr = SEMESTER_DATES.get(parts[1].toUpperCase());
        if (arr == null) throw new GeneralException(Status.UNSUPPORTED_SEMESTER_CODE);
        return arr;
    }

    /**
     * @description 요일과 학기 종료일을 기반으로 RFC 5545 표준 RRULE 문자열을 생성
     */
    private String generateRrule(DayOfWeek dayOfWeek, LocalDate semesterEnd) {
        String byDay = dayOfWeek.name(); // "MO", "TU" ...
        String until = semesterEnd.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        return "FREQ=WEEKLY;BYDAY=" + byDay + ";UNTIL=" + until;
    }

    /**
     * @description DayOfWeek Enum을 Java 표준 DayOfWeek Enum으로 변환
     */
    private java.time.DayOfWeek toJavaDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MO -> java.time.DayOfWeek.MONDAY;
            case TU -> java.time.DayOfWeek.TUESDAY;
            case WE -> java.time.DayOfWeek.WEDNESDAY;
            case TH -> java.time.DayOfWeek.THURSDAY;
            case FR -> java.time.DayOfWeek.FRIDAY;
            case SA -> java.time.DayOfWeek.SATURDAY;
            case SU -> java.time.DayOfWeek.SUNDAY;
        };
    }

    /**
     * @description 첫 수업의 정확한 날짜 및 시간 계산
     */
    private LocalDateTime combineDateTime(String semester, DayOfWeek dayOfWeek, LocalTime time) {
        LocalDate start = getSemesterPeriod(semester)[0];
        java.time.DayOfWeek target = toJavaDayOfWeek(dayOfWeek);
        // 학기 시작일로부터 가장 가까운 해당 요일을 찾음
        while (start.getDayOfWeek() != target) {
            start = start.plusDays(1);
        }
        return start.atTime(time);
    }
}