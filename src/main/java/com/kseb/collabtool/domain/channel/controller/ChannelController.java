package com.kseb.collabtool.domain.channel.controller;

import com.kseb.collabtool.domain.channel.dto.ChannelCreateRequest;
import com.kseb.collabtool.domain.channel.dto.ChannelResponse;
import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.service.ChannelService;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/channels")
    public ResponseEntity<ChannelResponse> createChannel(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ChannelCreateRequest request
    ) {
        User user = currentUser.getUser(); // 인증된 유저 엔티티
        Channel channel = channelService.createChannel(groupId, user.getId(), request);
        return ResponseEntity.ok(new ChannelResponse(channel));
    }

}
