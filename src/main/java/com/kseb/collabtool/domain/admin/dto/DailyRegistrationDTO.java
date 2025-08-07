package com.kseb.collabtool.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyRegistrationDTO {
    private LocalDate date;
    private long count;
}
