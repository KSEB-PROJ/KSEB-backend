package com.kseb.collabtool.domain.events.entity;

import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import com.kseb.collabtool.domain.timetable.entity.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Builder
@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USER or GROUP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OwnerType ownerType;

    // 그룹/유저 FK
    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 255)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startDatetime;

    @Column(nullable = false)
    private LocalDateTime endDatetime;

    @Column(nullable = false)
    private Boolean allDay;

    @Column(length = 255) //ex) FREQ=WEEKLY;BYDAY=MO,WE,FR
    private String rrule; // 반복 규칙

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(length = 20)
    private String courseCode;

    @Column(length = 50)
    private String professor;

    @Column(length = 10)
    private String semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "theme_color", nullable = false, length = 30)
    private String themeColor;  // HEX 컬러코드 (#xxxxxx)

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<EventParticipant> participants = new ArrayList<>();

    // 연관관계 메서드
    public void addParticipant(EventParticipant participant) {
        participants.add(participant);
        participant.setEvent(this);
    }
    // Event 엔티티 삭제(REMOVE) 시 관련 EventTask도 모두 같이 삭제
    //orphanRemoval = true 부모(Event)에서 자식(EventTask) 컬렉션에서 빠진(고아가 된) 객체 자동 삭제
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventTask> eventTasks = new ArrayList<>();

}
