package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.ParticipantStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantStatusUpdateRequest {
    @NotNull(message = "status 값은 필수입니다.")
    private ParticipantStatus status;
}
