package com.kseb.collabtool.domain.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.message.service.MessageService;
import com.kseb.collabtool.domain.notice.dto.NoticePromoteRequest;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.global.exception.ApiResponse;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}")
public class ChatController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/messages", consumes = "multipart/form-data")
    public ResponseEntity<Void> sendMessage(
            @PathVariable Long channelId,
            @RequestPart("message") String messageJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) throws Exception {
        Long userId = currentUser.getUser().getId();
        ChatRequest request = objectMapper.readValue(messageJson, ChatRequest.class);
        request.setChannelId(channelId);
        messageService.sendMessage(userId, request, files);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatResponse>> getMessages(
            @PathVariable Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        List<ChatResponse> responses = messageService.getMessages(channelId, userId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<ChatResponse> updateMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        request.setChannelId(channelId);
        ChatResponse response = messageService.updateMessage(userId, messageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/{messageId}/promote-notice")
    public ResponseEntity<NoticeResponse> promoteMessageToNotice(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @RequestBody NoticePromoteRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        NoticeResponse response = messageService.promoteMessageToNotice(
                channelId, messageId, userId, request.getPinnedUntil()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChannelChatHistoryForAi(
            @PathVariable("channelId") Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        List<ChatResponse> messages = messageService.getMessagesForAiSummary(channelId, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(messages));
    }
}
