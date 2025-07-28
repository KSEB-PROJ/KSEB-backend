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
// [수정] 공통 경로를 /api/channels/{channelId}로 통합
@RequestMapping("/api/channels/{channelId}")
public class ChatController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper; // (자동 주입, bean 등록됨)

    // --- 기존 API들 (경로 수정) ---

    // 메시지 + 파일 전송
    @PostMapping(value = "/messages", consumes = "multipart/form-data")
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


    /**
     * AI 챗봇 서버가 대화 요약 기능을 위해 채널의 전체 대화 내역을 조회하는 전용 API.
     * @param channelId 조회할 채널의 ID
     * @param userId    요청 헤더(X-User-ID)에 담겨있는 사용자 ID
     * @return 성공 시, 메시지 목록을 `data` 필드에 담은 ApiResponse 객체
     */
    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChannelChatHistoryForAi(
            @PathVariable("channelId") Long channelId,
            @RequestHeader("X-User-ID") Long userId) {

        List<ChatResponse> messages = messageService.getMessagesForAiSummary(channelId, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(messages));
    }
}