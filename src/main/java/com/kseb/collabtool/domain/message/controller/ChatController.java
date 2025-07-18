package com.kseb.collabtool.domain.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.message.service.MessageService;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.domain.notice.dto.NoticePromoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}/messages")
public class ChatController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper; // (자동 주입, bean 등록됨)

    // 메시지 + 파일 전송
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable Long channelId,
            @RequestPart("message") String messageJson, // String으로 받음!
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) throws Exception {
        Long userId = Long.parseLong(principal.getName());
        // JSON String을 DTO로 변환
        ChatRequest request = objectMapper.readValue(messageJson, ChatRequest.class);
        request.setChannelId(channelId);
        ChatResponse response = messageService.sendMessage(userId, request, file);
        return ResponseEntity.ok(response);
    }

    // 나머지 API 동일 (body로 받아도 됨)
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getMessages(
            @PathVariable Long channelId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        List<ChatResponse> responses = messageService.getMessages(channelId, userId);
        return ResponseEntity.ok(responses);
    }

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
