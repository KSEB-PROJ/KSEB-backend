package com.kseb.collabtool.domain.message.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.message.entity.Message;
import com.kseb.collabtool.domain.message.entity.MessageType;
import com.kseb.collabtool.domain.message.repository.MessageRepository;
import com.kseb.collabtool.domain.message.repository.MessageTypeRepository;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.domain.notice.entity.Notice;
import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final FilePathUtil filePathUtil;
    private final GroupMemberRepository groupMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void sendMessage(Long userId, ChatRequest request, List<MultipartFile> files) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        List<ChatResponse> responses = new ArrayList<>();

        // 1. 텍스트 메시지 처리
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            MessageType textType = messageTypeRepository.findByCode("TEXT")
                    .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "TEXT 메시지 타입을 찾을 수 없습니다."));
            Message textMessage = new Message();
            textMessage.setChannel(channel);
            textMessage.setUser(user);
            textMessage.setContent(request.getContent());
            textMessage.setMessageType(textType);
            responses.add(toResponse(messageRepository.save(textMessage), userId));
        }

        // 2. 파일 메시지 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String messageTypeCode = getMessageTypeCodeFromFile(file);
                MessageType messageType = messageTypeRepository.findByCode(messageTypeCode)
                        .orElseGet(() -> messageTypeRepository.findByCode("DOCUMENT")
                                .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "기본 DOCUMENT 메시지 타입을 찾을 수 없습니다.")));

                Message fileMessage = new Message();
                fileMessage.setChannel(channel);
                fileMessage.setUser(user);
                fileMessage.setMessageType(messageType);

                try {
                    String originalName = file.getOriginalFilename();
                    String ext = (originalName != null && originalName.contains(".")) ? originalName.substring(originalName.lastIndexOf('.')) : "";
                    String saveName = UUID.randomUUID().toString() + ext;
                    String savePath = filePathUtil.getChatFilePath(saveName);
                    String fileUrl = filePathUtil.getChatFileUrl(saveName);

                    Path path = Paths.get(savePath);
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());

                    fileMessage.setFileUrl(fileUrl);
                    fileMessage.setFileName(originalName);
                    responses.add(toResponse(messageRepository.save(fileMessage), userId));

                } catch (IOException e) {
                    throw new GeneralException(Status.INTERNAL_SERVER_ERROR, "파일 저장 실패: " + e.getMessage());
                }
            }
        }

        if (!responses.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/channels/" + channel.getId(),
                    Map.of("type", "NEW_MESSAGES", "payload", responses));
        }
    }
    
    @Transactional
    public ChatResponse updateMessage(Long userId, Long messageId, ChatRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }
        if (!message.getMessageType().getCode().equals("TEXT")) {
            throw new GeneralException(Status.BAD_REQUEST, "파일 메시지는 내용을 수정할 수 없습니다.");
        }

        message.setContent(request.getContent());
        Message updatedMessage = messageRepository.save(message);
        ChatResponse response = toResponse(updatedMessage, userId);
        
        messagingTemplate.convertAndSend("/topic/channels/" + message.getChannel().getId(),
                Map.of("type", "MESSAGE_UPDATE", "payload", response));

        return response;
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }

        message.setDeleted(true);
        message.setContent("삭제된 메시지입니다.");
        messageRepository.save(message);

        Long channelId = message.getChannel().getId();
        messagingTemplate.convertAndSend("/topic/channels/" + channelId,
                Map.of("type", "MESSAGE_DELETE", "payload", Map.of("id", message.getId())));
    }


    private String getMessageTypeCodeFromFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "DOCUMENT";
        if (contentType.startsWith("image/")) return "IMAGE";
        if (contentType.startsWith("video/")) return "VIDEO";
        return "DOCUMENT";
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getMessages(Long channelId, Long currentUserId) {
        List<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId);
        return messages.stream()
                .map(m -> toResponse(m, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeResponse promoteMessageToNotice(Long channelId, Long messageId, Long userId, LocalDateTime pinnedUntil) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getChannel().getId().equals(channelId) || !message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }

        Notice notice = new Notice();
        notice.setGroup(message.getChannel().getGroup());
        notice.setChannel(message.getChannel());
        notice.setUser(message.getUser());
        notice.setSourceMessage(message);
        notice.setContent(message.getContent());
        notice.setPinnedUntil(pinnedUntil);
        notice.setCreatedAt(LocalDateTime.now());

        Notice saved = noticeRepository.save(notice);
        return NoticeResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getMessagesForAiSummary(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        if (!groupMemberRepository.existsByGroupIdAndUserId(channel.getGroup().getId(), userId)) {
            throw new GeneralException(Status.FORBIDDEN, "해당 채널에 접근할 권한이 없습니다.");
        }
        List<Message> messages = messageRepository.findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(channelId);
        return messages.stream()
                .map(message -> toResponse(message, userId))
                .collect(Collectors.toList());
    }

    private ChatResponse toResponse(Message message, Long currentUserId) {
        if (message.isDeleted()) {
            return ChatResponse.builder()
                    .id(message.getId())
                    .channelId(message.getChannel().getId())
                    .content("삭제된 메시지입니다.")
                    .messageType("TEXT")
                    .createdAt(message.getCreatedAt())
                    .deleted(true)
                    .build();
        }

        return ChatResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .userId(message.getUser().getId())
                .userName(message.getUser().getName())
                .profileImgUrl(message.getUser().getProfileImg())
                .content(message.getContent())
                .messageType(message.getMessageType().getCode())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .createdAt(message.getCreatedAt())
                .deleted(false)
                .build();
    }
}