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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    //조회
    @GetMapping
    public ResponseEntity<List<EventResponse>> getMyEvents(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        List<EventResponse> events = userEventService.getAllEventsForUser(user.getId());
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
