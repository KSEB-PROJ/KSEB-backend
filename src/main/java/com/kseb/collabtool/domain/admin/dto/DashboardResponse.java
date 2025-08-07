package com.kseb.collabtool.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {
    private long totalUsers;
    private long todayRegisteredUsers;
    private long totalGroups;
}
