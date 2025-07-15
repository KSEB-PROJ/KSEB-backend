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

@Service
@RequiredArgsConstructor
public class CourseTimetableService {
    private final CourseTimetableRepository courseTimetableRepository;
    private final EventRepository eventRepository;

    // 학기 코드에 따른 [시작일, 종료일] (예시: S1, S2, SU, WI)
    private static final Map<String, LocalDate[]> SEMESTER_DATES = Map.of(
            "S1", new LocalDate[] { LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 21) },
            "SU", new LocalDate[] { LocalDate.of(2025, 6, 24), LocalDate.of(2025, 8, 12) },
            "S2", new LocalDate[] { LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 21) },
            "WI", new LocalDate[] { LocalDate.of(2025, 12, 24), LocalDate.of(2026, 2, 15) }
    );

    // ===== 전체 조회 (본인+학기별) =====
    @Transactional(readOnly = true)
    public List<CourseTimetableDto> getAll(Long userId, String semester) {
        if (semester == null || semester.isEmpty()) {
            return courseTimetableRepository.findAllByUserId(userId)
                    .stream().map(CourseTimetableDto::fromEntity).toList();
        }
        return courseTimetableRepository.findAllByUserIdAndSemester(userId, semester)
                .stream().map(CourseTimetableDto::fromEntity).toList();
    }

    // ===== 강의 시간표 등록 =====
    @Transactional
    public CourseTimetableDto create(CourseTimetableDto dto, Long userId) {
        boolean exists = courseTimetableRepository.existsOverlap(
                userId, dto.getSemester(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        if (exists) {
            throw new GeneralException(Status.TIMETABLE_OVERLAP);
        }

        // 학기 정보 파싱
        LocalDate[] period = getSemesterPeriod(dto.getSemester());
        LocalDate semesterStart = period[0];
        LocalDate semesterEnd = period[1];

        // RRULE 자동 생성 (매주 해당 요일, 학기 끝까지)
        String rrule = generateRrule(dto.getDayOfWeek(), semesterEnd);

        // 시간표 저장 (themeColor 필수)
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
                        .themeColor(dto.getThemeColor())   // ★ 추가!
                        .build());

        // Event에도 RRULE + themeColor 저장
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
                .themeColor(saved.getThemeColor()) // ★ 추가!
                .build());

        return CourseTimetableDto.fromEntity(saved);
    }

    // ===== 단일 조회(본인 데이터만) =====
    @Transactional(readOnly = true)
    public CourseTimetableDto get(Long id, Long userId) {
        CourseTimetable entity = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));
        return CourseTimetableDto.fromEntity(entity);
    }

    // ===== 강의 시간표 부분 수정(PATCH) =====
    @Transactional
    public CourseTimetableDto patch(Long id, CourseTimetableDto dto, Long userId) {
        CourseTimetable entity = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));

        boolean overlap = courseTimetableRepository.existsOverlap(
                userId, dto.getSemester(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        boolean changing =
                (!entity.getSemester().equals(dto.getSemester()) ||
                        !entity.getDayOfWeek().equals(dto.getDayOfWeek()) ||
                        !entity.getStartTime().equals(dto.getStartTime()) ||
                        !entity.getEndTime().equals(dto.getEndTime()));
        if (overlap && changing) {
            throw new GeneralException(Status.TIMETABLE_OVERLAP);
        }

        // 수정
        if (dto.getCourseCode() != null) entity.setCourseCode(dto.getCourseCode());
        if (dto.getCourseName() != null) entity.setCourseName(dto.getCourseName());
        if (dto.getProfessor() != null) entity.setProfessor(dto.getProfessor());
        if (dto.getSemester() != null) entity.setSemester(dto.getSemester());
        if (dto.getDayOfWeek() != null) entity.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartTime() != null) entity.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) entity.setEndTime(dto.getEndTime());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getThemeColor() != null) entity.setThemeColor(dto.getThemeColor()); // ★ 추가

        // RRULE도 새로 계산 (semester, dayOfWeek 바뀔 때)
        LocalDate[] period = getSemesterPeriod(entity.getSemester());
        String rrule = generateRrule(entity.getDayOfWeek(), period[1]);
        entity.setRrule(rrule);

        // Event도 같이 수정 (rrule, themeColor 적용)
        Event event = eventRepository.findCourseEvent(
                userId,
                OwnerType.USER,
                entity.getCourseName(),
                entity.getCourseCode(),
                entity.getSemester(),
                entity.getDayOfWeek()
        );
        if (event != null) {
            event.setTitle(entity.getCourseName());
            event.setCourseCode(entity.getCourseCode());
            event.setProfessor(entity.getProfessor());
            event.setStartDatetime(combineDateTime(entity.getSemester(), entity.getDayOfWeek(), entity.getStartTime()));
            event.setEndDatetime(combineDateTime(entity.getSemester(), entity.getDayOfWeek(), entity.getEndTime()));
            event.setLocation(entity.getLocation());
            event.setRrule(rrule);
            event.setThemeColor(entity.getThemeColor()); // ★ 추가
        }

        return CourseTimetableDto.fromEntity(entity);
    }

    // ===== 삭제 =====
    @Transactional
    public void delete(Long id, Long userId) {
        CourseTimetable timetable = courseTimetableRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new GeneralException(Status.TIMETABLE_NOT_FOUND));

        String title = timetable.getCourseName();
        String courseCode = timetable.getCourseCode();
        String semester = timetable.getSemester();
        DayOfWeek dayOfWeek = timetable.getDayOfWeek();

        courseTimetableRepository.deleteById(id);

        // 연동 Event도 같이 삭제
        eventRepository.deleteCourseEvent(
                userId,
                OwnerType.USER,
                title,
                courseCode,
                semester,
                dayOfWeek
        );
    }

    // === [유틸] ===
    // 학기코드에서 LocalDate[](start, end) 반환
    private LocalDate[] getSemesterPeriod(String semester) {
        String[] parts = semester.split("-");
        if (parts.length != 2) throw new GeneralException(Status.INVALID_SEMESTER_FORMAT);
        LocalDate[] arr = SEMESTER_DATES.get(parts[1]);
        if (arr == null) throw new GeneralException(Status.UNSUPPORTED_SEMESTER_CODE);
        return arr;
    }

    // RRULE 생성 (매주, UNTIL:학기끝)
    private String generateRrule(DayOfWeek dayOfWeek, LocalDate semesterEnd) {
        String byDay = dayOfWeek.name(); // "MO", "TU" ...
        String until = semesterEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'235959'Z'"));
        return "FREQ=WEEKLY;BYDAY=" + byDay + ";UNTIL=" + until;
    }

    // DayOfWeek 변환
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

    // 실제 "학기 시작일+요일+시간" 날짜 리턴 (한번만)
    private LocalDateTime combineDateTime(String semester, DayOfWeek dayOfWeek, LocalTime time) {
        LocalDate[] period = getSemesterPeriod(semester);
        LocalDate start = period[0];

        java.time.DayOfWeek target = toJavaDayOfWeek(dayOfWeek);
        // start에서 해당 요일까지 앞으로 감
        while (start.getDayOfWeek() != target) {
            start = start.plusDays(1);
        }
        return start.atTime(time);
    }
}
