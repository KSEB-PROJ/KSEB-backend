package com.kseb.collabtool.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyActivityDTO {
    private int hour;
    private long count;
}
