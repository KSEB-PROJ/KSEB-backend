package com.kseb.collabtool.domain.notice.controller;

import com.kseb.collabtool.domain.notice.dto.*;
import com.kseb.collabtool.domain.notice.service.NoticeService;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지 생성
    @PostMapping("/groups/{groupId}/notices")
    public NoticeResponse createNotice(
            @PathVariable Long groupId,
            @RequestBody NoticeCreateRequest req,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        return noticeService.createNotice(groupId, req, user);
    }

    // 공지 목록 조회
    @GetMapping("/groups/{groupId}/notices")
    public List<NoticeResponse> getNoticeList(@PathVariable Long groupId) {
        return noticeService.getNoticeList(groupId);
    }

    // 공지 상세 조회
    @GetMapping("/groups/{groupId}/notices/{noticeId}")
    public NoticeResponse getNotice(
            @PathVariable Long groupId,
            @PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    // 공지 수정
    @PatchMapping("/groups/{groupId}/notices/{noticeId}")
    public NoticeResponse updateNotice(
            @PathVariable Long groupId,
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequest req) {
        return noticeService.updateNotice(noticeId, req);
    }

    // 공지 삭제
    @DeleteMapping("/groups/{groupId}/notices/{noticeId}")
    public void deleteNotice(
            @PathVariable Long groupId,
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
    }

    // 공지 상단고정/해제
    @PatchMapping("/groups/{groupId}/notices/{noticeId}/pin")
    public NoticeResponse pinNotice(
            @PathVariable Long groupId,
            @PathVariable Long noticeId,
            @RequestBody NoticePinRequest req) {
        return noticeService.pinNotice(noticeId, req);
    }
}
