package com.kseb.collabtool.domain.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCreateResult {  //생성 시 스케줄 겹침 여부를 알려줌
    private Long eventId;
    private boolean hasOverlap; //겹치는 여부
}
