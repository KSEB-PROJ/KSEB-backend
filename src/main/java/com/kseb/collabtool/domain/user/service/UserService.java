package com.kseb.collabtool.domain.user.service;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.service.ActivityLogService;
import com.kseb.collabtool.domain.user.dto.PasswordChangeRequest;
import com.kseb.collabtool.domain.user.dto.UserResponse;
import com.kseb.collabtool.domain.user.dto.UserUpdateRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.util.FilePathUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilePathUtil filePathUtil;
    private final ActivityLogService activityLogService;

    @Transactional
    public User register(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new GeneralException(Status.USER_EMAIL_ALREADY_EXISTS);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 암호화
        user.setName(name);
        user.setCreatedAt(LocalDateTime.now());
        String defaultProfileImageUrl = filePathUtil.getProfileImageUrl("default-profile.png");
        user.setProfileImg(defaultProfileImageUrl);
        return userRepository.save(user);
    }

    @Transactional
    public UserResponse patchUser(Long userId, UserUpdateRequest dto, MultipartFile profileImg) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        boolean isUpdated = false;
        if (dto != null && dto.getName() != null && !dto.getName().isEmpty()) {
            user.setName(dto.getName());
            isUpdated = true;
        }

        if (profileImg != null && !profileImg.isEmpty()) {
            deleteProfileImageFile(user);
            try {
                String imageFileName = UUID.randomUUID() + "_" + profileImg.getOriginalFilename();
                String uploadFolderPath = filePathUtil.getProfileImagePath("");
                Path uploadPath = Paths.get(uploadFolderPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path imageFilePath = uploadPath.resolve(imageFileName);
                Files.write(imageFilePath, profileImg.getBytes());
                user.setProfileImg(filePathUtil.getProfileImageUrl(imageFileName));
                isUpdated = true;
            } catch (IOException e) {
                throw new GeneralException(Status.FILE_UPLOAD_FAILED, "파일 저장에 실패했습니다.");
            }
        }

        if (isUpdated) {
            activityLogService.saveLog(user, ActionType.USER_UPDATE_INFO, user.getId(), "Profile updated");
        }

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        deleteProfileImageFile(user);

        String defaultImgUrl = filePathUtil.getProfileImageUrl("default-profile.png");
        user.setProfileImg(defaultImgUrl);

        activityLogService.saveLog(user, ActionType.USER_UPDATE_INFO, user.getId(), "Profile image deleted");

        return UserResponse.fromEntity(user);
    }

    private void deleteProfileImageFile(User user) {
        String currentImgUrl = user.getProfileImg();
        String defaultImgUrl = filePathUtil.getProfileImageUrl("default-profile.png");

        if (currentImgUrl != null && !currentImgUrl.equals(defaultImgUrl)) {
            try {
                String fileName = currentImgUrl.substring(currentImgUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(filePathUtil.getProfileImagePath(fileName));
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                System.err.println("Failed to delete profile image file: " + e.getMessage());
            }
        }
    }

    @Transactional
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new GeneralException(Status.USER_PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        activityLogService.saveLog(user, ActionType.USER_UPDATE_INFO, user.getId(), "Password changed");
    }
}
