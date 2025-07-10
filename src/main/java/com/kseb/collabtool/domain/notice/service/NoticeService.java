package com.kseb.collabtool.domain.notice.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.notice.dto.*;
import com.kseb.collabtool.domain.notice.entity.Notice;
import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import com.kseb.collabtool.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final GroupRepository groupRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public NoticeResponse createNotice(Long groupId, NoticeCreateRequest req, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));
        Channel channel = channelRepository.findById(req.getChannelId())
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        if (req.getPinnedUntil() != null && req.getPinnedUntil().isBefore(LocalDateTime.now())) {
            throw new GeneralException(Status.INVALID_PINNED_UNTIL); // 추가 필요!
        }

        Notice notice = new Notice();
        notice.setGroup(group);
        notice.setChannel(channel);
        notice.setUser(user);
        notice.setContent(req.getContent());
        notice.setPinnedUntil(req.getPinnedUntil()); // <<<<<<<<<<<<<<<<<<<<<<<< 이 줄이 중요
        notice.setCreatedAt(LocalDateTime.now());

        noticeRepository.save(notice);
        return toResponse(notice);
    }

    public List<NoticeResponse> getNoticeList(Long groupId) {
        return noticeRepository.findByGroup_IdOrderByPinnedUntilDescCreatedAtDesc(groupId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public NoticeResponse getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .map(this::toResponse)
                .orElseThrow(() -> new GeneralException(Status.NOTICE_NOT_FOUND));
    }

    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeUpdateRequest req) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(Status.NOTICE_NOT_FOUND));

        notice.setContent(req.getContent());
        notice.setUpdatedAt(LocalDateTime.now());

        // pinnedUntil 값이 있으면 예외 처리 후 업데이트
        if (req.getPinnedUntil() != null) {
            if (req.getPinnedUntil().isBefore(LocalDateTime.now())) {
                throw new GeneralException(Status.INVALID_PINNED_UNTIL);
            }
            notice.setPinnedUntil(req.getPinnedUntil());
        }
        return toResponse(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    public NoticeResponse pinNotice(Long noticeId, NoticePinRequest req) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(Status.NOTICE_NOT_FOUND));
        // 2. 만료 시간이 이미 지났으면 예외
        if (req.getPinnedUntil() != null && req.getPinnedUntil().isBefore(LocalDateTime.now())) {
            throw new GeneralException(Status.INVALID_PINNED_UNTIL); // 추가 필요!
        }
        notice.setPinnedUntil(req.getPinnedUntil());
        return toResponse(notice);
    }

    private NoticeResponse toResponse(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .groupId(notice.getGroup() != null ? notice.getGroup().getId() : null)
                .channelId(notice.getChannel() != null ? notice.getChannel().getId() : null)
                .userId(notice.getUser() != null ? notice.getUser().getId() : null)
                .content(notice.getContent())
                .sourceMessageId(notice.getSourceMessage() != null ? notice.getSourceMessage().getId() : null)
                .pinnedUntil(notice.getPinnedUntil())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

}
