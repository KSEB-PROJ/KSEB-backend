package com.kseb.collabtool.domain.groups.controller;

import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupDetailDto;
import com.kseb.collabtool.domain.groups.dto.GroupListDto;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.service.GroupService;
import com.kseb.collabtool.global.security.CustomUserDetails;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    private final UserRepository userRepository;


    // 그룹 생성
    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(   //ApiResponse<GroupResponse>
            @RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        User owner = currentUser.getUser();
        GroupResponse response = groupService.createGroup(request, owner);
        return ResponseEntity.ok(response);
    }

    // 그룹 리스트 조회
    @GetMapping("")
    public ResponseEntity<List<GroupListDto>> getMyGroups(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        List<GroupListDto> groups = groupService.getGroupsByUser(user.getId());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> getGroupDetail(@PathVariable Long groupId) {
        GroupDetailDto detail = groupService.getGroupDetail(groupId);
        return ResponseEntity.ok(detail);
    }

    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
                                         @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        groupService.deleteGroupAndAllData(groupId, user);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}

