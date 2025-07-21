package com.kseb.collabtool.domain.events.controller;

import com.kseb.collabtool.domain.events.dto.*;
import com.kseb.collabtool.domain.events.service.GroupEventService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/events")
public class GroupEventController {

    private final GroupEventService groupEventService;

    @PostMapping
    public ResponseEntity<EventCreateResult> createGroupEvent(
            @PathVariable Long groupId,
            @RequestBody @Valid GroupEventCreateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        // groupId를 dto에 주입 (path와 body의 일치 보장)
        User user = currentUser.getUser();
        EventCreateResult result = groupEventService.createGroupEvent(groupId, dto, user.getId());
        return ResponseEntity.ok(result);
    }

    //개인 event가 아니라 그룹 eventId 넣어서 삭제해야됨
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteGroupEvent(
            @PathVariable Long groupId,
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        groupEventService.deleteGroupEvent(groupId, eventId, user.getId());
        //롤백 성공 메세지 넣을까말까 했는데, 그냥 삭제할 때도 사용할거라 프론트쪽에서 처리해주면 좋을듯
        return ResponseEntity.noContent().build();
    }

    //조회
    @GetMapping
    public ResponseEntity<List<EventResponse>> getGroupEvents(
            @PathVariable Long groupId
    ) {
        List<EventResponse> events = groupEventService.getGroupEvents(groupId);
        return ResponseEntity.ok(events);
    }

    //status
    @PutMapping("/{eventId}/participants/me/status")
    public ResponseEntity<Void> updateMyStatus(
            @PathVariable Long groupId,
            @PathVariable Long eventId,
            @RequestBody @Valid ParticipantStatusUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId(); // 현재 로그인한 사용자 ID
        groupEventService.updateParticipantStatus(groupId, eventId, userId, dto.getStatus());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Void> patchGroupEvent(
            @PathVariable Long groupId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequest dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        groupEventService.updateGroupEvent(groupId, eventId, userId, dto);
        return ResponseEntity.noContent().build();
    }



}
