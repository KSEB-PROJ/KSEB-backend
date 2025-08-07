package com.kseb.collabtool.domain.admin.controller;

import com.kseb.collabtool.domain.admin.dto.*;
import com.kseb.collabtool.domain.admin.service.AdminService;
import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.user.entity.Role;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserAdminResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getUsers(pageable));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestParam Role role, @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.updateUserRole(userId, role, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteUser(userId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<Page<GroupAdminResponse>> getGroups(Pageable pageable) {
        return ResponseEntity.ok(adminService.getGroups(pageable));
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteGroup(groupId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<LogResponse>> getLogs(
            Pageable pageable,
            @RequestParam(required = false) String actorName,
            @RequestParam(required = false) List<ActionType> actionTypes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getLogs(pageable, actorName, actionTypes, startDate, endDate));
    }

    @GetMapping("/stats/daily-registrations")
    public ResponseEntity<List<DailyRegistrationDTO>> getDailyRegistrations(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(adminService.getDailyRegistrations(days));
    }

    @GetMapping("/stats/hourly-activity")
    public ResponseEntity<List<HourlyActivityDTO>> getHourlyActivity(@RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(adminService.getHourlyActivity(hours));
    }

    @GetMapping("/stats/content-distribution")
    public ResponseEntity<ContentTypeDistributionDTO> getContentTypeDistribution() {
        return ResponseEntity.ok(adminService.getContentTypeDistribution());
    }
}
