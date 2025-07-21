package com.kseb.collabtool.domain.channel.dto;

import com.kseb.collabtool.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelListDto {
    private Long id;
    private String name;
    private Short channelTypeId;
    private String channelTypeCode;
    private boolean isSystem;

    public ChannelListDto(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.channelTypeId=channel.getChannelType().getId();
        this.channelTypeCode = channel.getChannelType().getCode();
        this.isSystem = channel.getIsSystem();
    }
}

