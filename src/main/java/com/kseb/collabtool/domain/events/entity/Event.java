package com.kseb.collabtool.domain.events.entity;

import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 255)
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

    // 양방향 연관관계 => 있으면 관리하기 편하다
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventTask> tasks = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addTask(EventTask task) {
        tasks.add(task);
        task.setEvent(this);
    }

}
