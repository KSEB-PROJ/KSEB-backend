package com.kseb.collabtool.domain.events.controller;

import com.kseb.collabtool.domain.events.dto.EventTaskResponse;
import com.kseb.collabtool.domain.events.dto.TaskResponse;
import com.kseb.collabtool.domain.events.dto.UpdateTaskRequest;
import com.kseb.collabtool.domain.events.service.EventTaskService;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final EventTaskService eventTaskService;

    //내 전체 Task 조회 (담당자 기준) 어느 이벤트에 해당하는 정보까지 전체 조회

    @GetMapping("/my") //개인 task 조회
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long user = currentUser.getUser().getId();
        List<TaskResponse> response = eventTaskService.getTasksByAssignee(user, user);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{taskId}")
    public ResponseEntity<EventTaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        EventTaskResponse response = eventTaskService.updateTask(taskId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        eventTaskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group/{groupId}") //해당 그룹에 해당하는 모든 task 조회
    public ResponseEntity<List<TaskResponse>> getGroupTasks(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        List<TaskResponse> response = eventTaskService.getTasksByGroup(groupId, userId);
        return ResponseEntity.ok(response);
    }
}
