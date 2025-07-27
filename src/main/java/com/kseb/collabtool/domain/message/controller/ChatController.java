package com.kseb.collabtool.domain.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.message.service.MessageService;
import com.kseb.collabtool.domain.notice.dto.NoticePromoteRequest;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.global.security.CustomUserDetails; // [수정] CustomUserDetails 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // [수정] AuthenticationPrincipal 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}/messages")
public class ChatController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper; // (자동 주입, bean 등록됨)

    // 메시지 + 파일 전송
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<List<ChatResponse>> sendMessage(
            //파일 다중 업로드를 위해 CHAT 부분 전부 수정합니당
            // 반환 타입을 List<ChatResponse>로
            @PathVariable Long channelId,
            @RequestPart("message") String messageJson,
            // 단일 파일(file) 대신 여러 파일(files)을 리스트로 받음
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) throws Exception {
        Long userId = currentUser.getUser().getId();
        ChatRequest request = objectMapper.readValue(messageJson, ChatRequest.class);
        request.setChannelId(channelId);
        // files 리스트를 서비스로 전달하고, 여러 개의 응답을 받을 수 있도록 수정
        List<ChatResponse> responses = messageService.sendMessage(userId, request, files);
        return ResponseEntity.ok(responses);
    }

    // 나머지 API 동일 (body로 받아도 됨)
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getMessages(
            @PathVariable Long channelId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        List<ChatResponse> responses = messageService.getMessages(channelId, userId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{messageId}")
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

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{messageId}/promote-notice")
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
}