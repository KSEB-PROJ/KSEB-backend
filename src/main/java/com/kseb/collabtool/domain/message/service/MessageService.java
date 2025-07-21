package com.kseb.collabtool.domain.message.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import com.kseb.collabtool.domain.filemeta.repository.FileRepository;
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
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final NoticeRepository noticeRepository;
    private final MessageRepository messageRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FilePathUtil filePathUtil;

    // 메세지 리스트로 변경
    @Transactional
    public List<ChatResponse> sendMessage(Long userId, ChatRequest request, List<MultipartFile> files) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        List<ChatResponse> responses = new ArrayList<>();

        // 1. 텍스트 메시지 처리 (내용이 있을 경우에만)
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            // 메시지 타입 텍스트인지 조회
            MessageType textType = messageTypeRepository.findByCode("TEXT") // 코드로 조회
                    .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "TEXT 메시지 타입을 찾을 수 없습니다."));
            Message textMessage = new Message();
            textMessage.setChannel(channel);
            textMessage.setUser(user);
            textMessage.setContent(request.getContent());
            textMessage.setMessageType(textType);
            Message savedTextMessage = messageRepository.save(textMessage);
            //리스트에 추가
            responses.add(toResponse(savedTextMessage, userId));
        }

        // 2. 파일 메시지 처리 (파일이 있을 경우에만)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // 파일의 MIME 타입 기반으로 적절한 MessageType 결정.
                String messageTypeCode = getMessageTypeCodeFromFile(file);
                MessageType messageType = messageTypeRepository.findByCode(messageTypeCode)
                        .orElseGet(() -> messageTypeRepository.findByCode("DOCUMENT") // 타입을 모를 경우 'DOCUMENT'를 기본값으로 사용
                                .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "기본 DOCUMENT 메시지 타입을 찾을 수 없습니다.")));

                Message fileMessage = new Message();
                fileMessage.setChannel(channel);
                fileMessage.setUser(user);
                fileMessage.setMessageType(messageType); // 동적으로 결정된 메시지 타입 설정

                try {
                    String originalName = file.getOriginalFilename();
                    String ext = "";
                    if (originalName != null && originalName.contains(".")) {
                        ext = originalName.substring(originalName.lastIndexOf('.'));
                    }
                    String saveName = UUID.randomUUID().toString() + ext;
                    String savePath = filePathUtil.getChatFilePath(saveName);
                    String fileUrl = filePathUtil.getChatFileUrl(saveName);

                    Path path = Paths.get(savePath);
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());

                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setChannel(channel);
                    fileEntity.setUser(user);
                    fileEntity.setFileUrl(fileUrl);
                    fileEntity.setFileName(file.getOriginalFilename());
                    fileEntity.setFileType(getFileExt(file.getOriginalFilename()));
                    fileEntity.setMimeType(file.getContentType());
                    fileEntity.setFileSize(file.getSize());
                    fileRepository.save(fileEntity);

                    fileMessage.setFileUrl(fileUrl);
                    fileMessage.setFileName(file.getOriginalFilename());

                    Message savedFileMessage = messageRepository.save(fileMessage);
                    responses.add(toResponse(savedFileMessage, userId));

                } catch (IOException e) {
                    throw new GeneralException(Status.INTERNAL_SERVER_ERROR, "파일 저장 실패: " + e.getMessage());
                }
            }
        }

        return responses;
    }

    /**
     * MultipartFile의 ContentType(MIME Type)을 확인하여
     * 우리 시스템의 MessageType 코드(IMAGE, VIDEO, DOCUMENT 등)를 반환.
     * @param file 업로드된 파일
     * @return MessageType 코드 문자열
     */
    private String getMessageTypeCodeFromFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return "DOCUMENT"; // 타입을 알 수 없으면 기본 '문서' 타입으로 처리
        }
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        }
        if (contentType.startsWith("video/")) {
            return "VIDEO";
        }
        return "DOCUMENT"; // 그 외 모든 경우는 '문서' 타입으로 처리
    }

    // 파일 확장자 추출 함수
    private String getFileExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getMessages(Long channelId, Long currentUserId) {
        List<Message> messages = messageRepository.findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(channelId);
        return messages.stream()
                .map(m -> toResponse(m, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatResponse updateMessage(Long userId, Long messageId, ChatRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }
        if (request.getContent() != null) message.setContent(request.getContent());
        if (request.getFileUrl() != null) message.setFileUrl(request.getFileUrl());
        if (request.getFileName() != null) message.setFileName(request.getFileName());
        if (request.getMessageTypeId() != null) {
            MessageType messageType = messageTypeRepository.findById(request.getMessageTypeId())
                    .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST));
            message.setMessageType(messageType);
        }
        return toResponse(message, userId);
    }

    @Transactional
    public NoticeResponse promoteMessageToNotice(
            Long channelId, Long messageId, Long userId, LocalDateTime pinnedUntil
    ) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getChannel().getId().equals(channelId)) {
            throw new GeneralException(Status.BAD_REQUEST);
        }
        if (!message.getUser().getId().equals(userId)) {
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

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }

        if (message.getFileUrl() != null) {
            Optional<FileEntity> fileOpt = fileRepository.findByFileUrl(message.getFileUrl());
            fileOpt.ifPresent(file -> {
                try {
                    String fileName = file.getFileUrl().substring(file.getFileUrl().lastIndexOf("/") + 1);
                    Path filePath = Paths.get(filePathUtil.getChatFilePath(fileName));
                    Files.deleteIfExists(filePath);
                    fileRepository.delete(file);
                } catch (IOException e) {
                    System.err.println("파일 삭제 실패: " + e.getMessage());
                }
            });
        }
        messageRepository.delete(message);
    }

    private ChatResponse toResponse(Message message, Long currentUserId) {
        return ChatResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .userId(message.getUser().getId())
                .userName(message.getUser().getName())
                .content(message.getContent())
                .messageType(message.getMessageType().getCode())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .isMine(message.getUser().getId().equals(currentUserId))
                .createdAt(message.getCreatedAt())
                .build();
    }
}