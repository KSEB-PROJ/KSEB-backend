package com.kseb.collabtool.domain.timetable.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "timetable_types")
public class TimetableType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // TINYINT PK

    @Column(nullable = false, unique = true, length = 32)
    private String code; // LECTURE, PARTTIME, MANUAL

    @Column(nullable = false, length = 64)
    private String name; // 표시명(강의/알바/약속 등)

}
