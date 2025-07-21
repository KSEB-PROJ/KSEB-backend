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

    /**
     * 사용자 이름과 프로필 이미지를 함께 수정
     *
     * @param userId     현재 사용자 ID
     * @param dto        변경할 이름 정보
     * @param profileImg 변경할 프로필 이미지 파일
     * @return UserResponse 업데이트된 사용자 정보
     */
    @Transactional
    public UserResponse patchUser(Long userId, UserUpdateRequest dto, MultipartFile profileImg) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        // 1. 이름 변경 (dto가 null이 아니고, 이름이 실제로 존재할 때)
        if (dto != null && dto.getName() != null && !dto.getName().isEmpty()) {
            user.setName(dto.getName());
        }

        // 2. 프로필 이미지 변경 (profileImg가 null이 아니고 비어있지 않을 때)
        if (profileImg != null && !profileImg.isEmpty()) {
            deleteProfileImageFile(user); // 기존 이미지 파일 삭제

            try {
                String imageFileName = UUID.randomUUID() + "_" + profileImg.getOriginalFilename();

                // 1. 설정 파일에 명시된 폴더 경로를 가져옵니다. (예: "uploads/profile-images/")
                String uploadFolderPath = filePathUtil.getProfileImagePath("");

                // 2. Path 객체를 생성합니다.
                Path uploadPath = Paths.get(uploadFolderPath);

                // 3. (핵심) 디렉토리가 존재하지 않으면 생성합니다.
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. 최종 파일 경로를 결정하고 파일을 저장합니다.
                Path imageFilePath = uploadPath.resolve(imageFileName);
                Files.write(imageFilePath, profileImg.getBytes());

                user.setProfileImg(filePathUtil.getProfileImageUrl(imageFileName));

            } catch (IOException e) {
                // 파일 저장 중 I/O 예외 발생 시, 구체적인 에러 메시지와 함께 예외를 던집니다.
                throw new GeneralException(Status.FILE_UPLOAD_FAILED, "파일 저장에 실패했습니다. 서버 경로 및 권한을 확인해주세요.");
            }
        }

        return UserResponse.fromEntity(user);
    }


    @Transactional
    public UserResponse deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        // 실제 파일 삭제
        deleteProfileImageFile(user);

        // DB에서는 기본 이미지 URL로 변경
        String defaultImgUrl = filePathUtil.getProfileImageUrl("default-profile.png");
        user.setProfileImg(defaultImgUrl);

        return UserResponse.fromEntity(user);
    }

    // (Helper Method) 실제 이미지 파일을 삭제.
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
