package com.kseb.collabtool.domain.message.controller;

import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.message.service.MessageService;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.domain.notice.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kseb.collabtool.domain.notice.dto.NoticePromoteRequest;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}/messages")
public class ChatController {

    private final MessageService messageService;

    // 메시지 전송
    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable Long channelId,
            @RequestBody ChatRequest request,
            Principal principal // 인증된 사용자 정보
    ) {
        Long userId = Long.parseLong(principal.getName());
        request.setChannelId(channelId);
        ChatResponse response = messageService.sendMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    // 메시지 목록 조회
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getMessages(
            @PathVariable Long channelId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        List<ChatResponse> responses = messageService.getMessages(channelId, userId);
        return ResponseEntity.ok(responses);
    }

    // 메시지 수정
    @PatchMapping("/{messageId}")
    public ResponseEntity<ChatResponse> updateMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestBody ChatRequest request,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        request.setChannelId(channelId);
        ChatResponse response = messageService.updateMessage(userId, messageId, request);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{messageId}/promote-notice")
    public ResponseEntity<NoticeResponse> promoteMessageToNotice(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestBody NoticePromoteRequest request,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        NoticeResponse response = messageService.promoteMessageToNotice(
                channelId, messageId, userId, request.getPinnedUntil()
        );
        return ResponseEntity.ok(response);
    }
}