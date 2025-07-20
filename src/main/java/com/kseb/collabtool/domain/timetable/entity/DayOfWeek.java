package com.kseb.collabtool.domain.timetable.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DayOfWeek {
    MO, TU, WE, TH, FR, SA, SU;
}
