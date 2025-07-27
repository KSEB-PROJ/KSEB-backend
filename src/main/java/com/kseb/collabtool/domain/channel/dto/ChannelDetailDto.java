package com.kseb.collabtool.domain.channel.dto;

import com.kseb.collabtool.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDetailDto {
    private Long id;
    private String name;
    private Short channelTypeId;
    private String channelTypeCode;
    private String channelTypeName;
    private boolean isSystem;

    public ChannelDetailDto(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.channelTypeId = channel.getChannelType().getId();
        this.channelTypeCode = channel.getChannelType().getCode();
        this.channelTypeName = channel.getChannelType().getName();
        this.isSystem = channel.getIsSystem();
    }
}
