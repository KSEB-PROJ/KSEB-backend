package com.kseb.collabtool.domain.user.service;

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

    //유저 이름 변경
    @Transactional
    public UserResponse patchUser(Long userId, UserUpdateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        return UserResponse.fromEntity(user);
    }

    //프로필 변경
    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile profileImg) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        String imageFileName = UUID.randomUUID() + "_" + profileImg.getOriginalFilename();

        Path imageFilePath = Paths.get(filePathUtil.getProfileImagePath(imageFileName));

        try {
            Files.write(imageFilePath, profileImg.getBytes());
        } catch (Exception e) {
            throw new GeneralException(Status.FILE_UPLOAD_FAILED);
            //throw new RuntimeException("파일 저장 실패", e);
        }

        user.setProfileImg(filePathUtil.getProfileImageUrl(imageFileName));
        return UserResponse.fromEntity(user);
    }
    //프로필 삭제 -> 삭제 시 기존 프로필로 변경
    @Transactional
    public UserResponse deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        String currentImgUrl = user.getProfileImg();
        String defaultImgUrl = filePathUtil.getProfileImageUrl("default-profile.png");

        // 실제 파일 삭제 (기본이미지 아닐 때만)
        if (currentImgUrl != null && !currentImgUrl.equals(defaultImgUrl)) {
            try {
                // 파일명 추출
                String fileName = currentImgUrl.substring(currentImgUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(filePathUtil.getProfileImagePath(fileName));
                Files.deleteIfExists(filePath); // 없으면 패스
            } catch (Exception e) {
                throw new GeneralException(Status.FILE_DELETE_FAILED);
            }
        }

        //기본이미지 URL로 변경
        user.setProfileImg(defaultImgUrl);

        return UserResponse.fromEntity(user);
    }

    //유저 모든 정보를 조회함
    @Transactional
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        return UserResponse.fromEntity(user);
    }

    //유저 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new GeneralException(Status.USER_PASSWORD_MISMATCH); //"현재 비밀번호가 일치하지 않습니다."
        }

        // 새 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
