package com.kseb.collabtool.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentTypeDistributionDTO {
    private long groupCount;
    private long channelCount;
    private long noticeCount;
    private long eventCount;
}
