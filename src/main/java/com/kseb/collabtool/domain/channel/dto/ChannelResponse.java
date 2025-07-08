package com.kseb.collabtool.domain.channel.dto;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
public class ChannelResponse {
    private Long id;
    private String name;
    private Short channelTypeId;
    private String channelTypeCode;
    private String channelTypeName;
    private boolean isSystem;
    private LocalDateTime createdAt;

    public static ChannelResponse fromEntity(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getChannelType().getId(),
                channel.getChannelType().getCode(),
                channel.getChannelType().getName(),
                channel.getIsSystem(),
                channel.getCreatedAt()
        );
    }
}
