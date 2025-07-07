package com.kseb.collabtool.domain.channel.dto;

import com.kseb.collabtool.domain.channel.entity.Channel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChannelResponse {
    private Long id;
    private String name;
    private Short channelTypeId;
    private String channelTypeCode;
    private String channelTypeName;
    private boolean isSystem;
    private LocalDateTime createdAt;

    public ChannelResponse(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.channelTypeId = channel.getChannelType().getId();
        this.channelTypeCode = channel.getChannelType().getCode();
        this.channelTypeName = channel.getChannelType().getName();
        this.isSystem = channel.getIsSystem();
        this.createdAt=channel.getCreatedAt();
    }
}
