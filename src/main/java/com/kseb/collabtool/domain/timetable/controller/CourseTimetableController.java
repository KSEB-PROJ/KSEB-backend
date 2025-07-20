package com.kseb.collabtool.domain.timetable.controller;

import com.kseb.collabtool.domain.timetable.dto.CourseTimetableDto;
import com.kseb.collabtool.domain.timetable.service.CourseTimetableService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class CourseTimetableController {

    private final CourseTimetableService service;

    // 1. 전체 조회
    @GetMapping
    public ResponseEntity<List<CourseTimetableDto>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String semester) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(service.getAll(userId, semester));
    }

    // 2. 생성
    @PostMapping
    public ResponseEntity<CourseTimetableDto> create(
            @RequestBody CourseTimetableDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(service.create(dto, userId));
    }

    // 3. 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        service.delete(id, userId);
        return ResponseEntity.ok().build();
    }

    // 4. 수정(PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<CourseTimetableDto> patch(
            @PathVariable Long id,
            @RequestBody CourseTimetableDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(service.patch(id, dto, userId));
    }
}


