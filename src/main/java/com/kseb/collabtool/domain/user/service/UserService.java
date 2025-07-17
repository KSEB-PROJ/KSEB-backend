package com.kseb.collabtool.domain.user.service;

import com.kseb.collabtool.domain.user.dto.UserResponse;
import com.kseb.collabtool.domain.user.dto.UserUpdateRequest;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.util.FilePathUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
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

    @Transactional
    public UserResponse patchUser(Long userId, UserUpdateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        return UserResponse.from(user);
    }

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
        return UserResponse.from(user);
    }


}
