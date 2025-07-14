package com.kseb.collabtool.domain.groups.controller;

import com.kseb.collabtool.domain.groups.dto.GroupJoinByCodeRequest;
import com.kseb.collabtool.domain.groups.dto.GroupMemberResponse;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.service.GroupMemberService;
import com.kseb.collabtool.global.security.CustomUserDetails;
import com.kseb.collabtool.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping("/{groupId}/members")
    public List<GroupMemberResponse> getGroupMembers(@PathVariable Long groupId) {
        return groupMemberService.getMembersByGroupId(groupId);
    }

    @PostMapping("/join")
    public ResponseEntity<GroupResponse> joinGroupByCode(
            @RequestBody GroupJoinByCodeRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        GroupResponse response = groupMemberService.joinGroupByInviteCode(request.getInviteCode(), user);
        return ResponseEntity.ok(response);
    }


    //나중에 고민중
    //리더는 한명만..? 만약 리더가 맴버를 리더로 승격시키면 리더는
    /*@PatchMapping("/{groupId}/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long groupId, //방 ID
            @PathVariable Long memberId, // 승격시킬려는 멤버 ID
            @AuthenticationPrincipal CustomUserDetails currentUser) { //현재 로그인된 멤버+그룹리더
        groupMemberService.changeRole(groupId, memberId, currentUser.getUser());
        return ResponseEntity.ok().build();
    }*/
}
