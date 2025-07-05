package com.kseb.collabtool.domain.groups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberRequest { //인원 추가 생성
    private Long userId;
}
