package com.kseb.collabtool.domain.events.controller;

import com.kseb.collabtool.domain.events.dto.EventCreateResult;
import com.kseb.collabtool.domain.events.dto.EventResponse;
import com.kseb.collabtool.domain.events.dto.EventUpdateRequest;
import com.kseb.collabtool.domain.events.dto.UserEventCreateRequest;
import com.kseb.collabtool.domain.events.service.UserEventService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users/me/events")
@RequiredArgsConstructor
public class UserEventController {

    private final UserEventService userEventService;

    @PostMapping
    public ResponseEntity<EventCreateResult> createUserEvent(
            @RequestBody @Valid UserEventCreateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        EventCreateResult result = userEventService.createUserEvent(dto, user.getId());
        return ResponseEntity.ok(result);
    }

    // 이벤트 삭제 (롤백/사용자 취소)
    /*
    겹침 경고에서 취소/롤백 버튼 누르면 → DELETE /api/users/me/events/{eventId} 호출
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteUserEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        userEventService.deleteUserEvent(eventId, user.getId());
        //롤백 성공 메세지 넣을까말까 했는데, 그냥 삭제할 때도 사용할거라 프론트쪽에서 처리해주면 좋을듯
        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 로그인한 사용자의 모든 일정(개인+그룹)을 조회합니다.
     * @param currentUser 현재 인증된 사용자 정보
     * @param startDate (선택) 조회 시작일 (ISO 8601 형식, 예: 2023-01-01T00:00:00)
     * @param endDate (선택) 조회 종료일 (ISO 8601 형식, 예: 2023-01-31T23:59:59)
     * @return 일정 목록
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getMyEvents(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        User user = currentUser.getUser();
        List<EventResponse> events = userEventService.getAllEventsForUser(user.getId(), startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Void> patchUserEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        userEventService.updateUserEvent(eventId, userId, dto);
        return ResponseEntity.noContent().build();
    }


}