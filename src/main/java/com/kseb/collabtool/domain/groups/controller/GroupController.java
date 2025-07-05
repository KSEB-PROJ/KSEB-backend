package com.kseb.collabtool.domain.groups.controller;

import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.service.GroupService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    private final UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupCreateRequest request, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupService.createGroup(request,owner);
        return ResponseEntity.ok(new GroupResponse(group));
    }

}
