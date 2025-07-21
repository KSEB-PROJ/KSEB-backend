package com.kseb.collabtool.domain.channel.controller;

import com.kseb.collabtool.domain.channel.dto.*;
import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.service.ChannelService;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> createChannel(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ChannelCreateRequest request
    ) {
        User user = currentUser.getUser(); // 인증된 유저 엔티티
        ChannelResponse response = channelService.createChannel(groupId, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChannelListDto>> getChannels(
            @PathVariable Long groupId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        List<ChannelListDto> channels = channelService.getChannels(groupId, user.getId());
        return ResponseEntity.ok(channels);
    }


    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDetailDto> getChannelDetail(
            @PathVariable Long groupId,
            @PathVariable Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        ChannelDetailDto detail = channelService.getChannelDetail(groupId, channelId, user.getId());
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<Void> updateChannel(
            @PathVariable Long groupId,
            @PathVariable Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody ChannelUpdateRequest request
    ) {
        User user = currentUser.getUser();
        channelService.updateChannel(groupId, channelId, user.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable Long groupId,
            @PathVariable Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = currentUser.getUser();
        channelService.deleteChannel(groupId, channelId, user.getId());
        return ResponseEntity.noContent().build(); // HTTP 204 상태코드로 성공했지만 반환 데이터는 없음
    }







}
