package com.kseb.collabtool.domain.timetable.entity;

import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_timetables")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CourseTimetable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", length = 20, nullable = false)
    private String courseCode;

    @Column(name = "course_name", length = 100, nullable = false)
    private String courseName;

    @Column(length = 50)
    private String professor;

    @Column(length = 10, nullable = false)
    private String semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 2)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(length = 100)
    private String location;

    @Column(length = 255)
    private String rrule;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "theme_color", nullable = false, length = 7)
    private String themeColor;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
