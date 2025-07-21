package com.kseb.collabtool.domain.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCreateRequest {
    private String name;
    private Short channelTypeId;     // 채널타입의 PK(id) (1:CHAT, 2:CALENDAR, 3:NOTICE)
}
