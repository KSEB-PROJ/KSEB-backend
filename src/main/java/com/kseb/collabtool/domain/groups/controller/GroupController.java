package com.kseb.collabtool.domain.groups.controller;

import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupDetailDto;
import com.kseb.collabtool.domain.groups.dto.GroupListDto;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.service.GroupService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    private final UserRepository userRepository;

    // 현재 인증 유저 조회
    private User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //그룹 생성
    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupCreateRequest request, Principal principal) {
        User owner = getCurrentUser(principal);
        Group group = groupService.createGroup(request, owner);
        return ResponseEntity.ok(GroupResponse.fromEntity(group));
    }
    //그룹 리스트 조회
    @GetMapping("")
    public ResponseEntity<List<GroupListDto>> getMyGroups(Principal principal) {
        User user = getCurrentUser(principal);
        List<GroupListDto> groups = groupService.getGroupsByUser(user.getId());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> getGroupDetail(@PathVariable Long groupId) {
        GroupDetailDto detail = groupService.getGroupDetail(groupId);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId, Principal principal) {
        User user = getCurrentUser(principal); // 앞에서 만든 헬퍼 메서드
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.noContent().build();
    }
}
