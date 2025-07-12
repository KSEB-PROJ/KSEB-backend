package com.kseb.collabtool.domain.events.controller;

import com.kseb.collabtool.domain.events.dto.MyTaskResponse;
import com.kseb.collabtool.domain.events.service.EventTaskService;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks/my")
public class MyTaskController {
    private final EventTaskService eventTaskService;

    //내 전체 Task 조회 (담당자 기준) 어느 이벤트에 해당하는 정보까지 전체 조회
    @GetMapping
    public ResponseEntity<List<MyTaskResponse>> getMyTasks(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long user = currentUser.getUser().getId();
        List<MyTaskResponse> response = eventTaskService.getTasksByAssignee(user, user);
        return ResponseEntity.ok(response);
    }
}
