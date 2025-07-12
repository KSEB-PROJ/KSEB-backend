package com.kseb.collabtool.domain.events.controller;

import com.kseb.collabtool.domain.events.dto.EventTaskCreateRequest;
import com.kseb.collabtool.domain.events.dto.EventTaskResponse;
import com.kseb.collabtool.domain.events.service.EventTaskService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/tasks")
public class EventTaskController {

    private final EventTaskService eventTaskService;

    @PostMapping
    public ResponseEntity<EventTaskResponse> addTask(
            @PathVariable Long eventId,
            @RequestBody EventTaskCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        EventTaskResponse response = eventTaskService.addTaskToEvent(eventId, request, user.getId());
        return ResponseEntity.ok(response);
    }
}
