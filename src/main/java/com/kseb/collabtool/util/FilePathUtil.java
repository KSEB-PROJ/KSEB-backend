package com.kseb.collabtool.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilePathUtil {
    @Value("${file.profile-image-folder}")
    private String profileImageFolder;

    @Value("${file.profile-image-url-prefix}")
    private String profileImageUrlPrefix;

    @Value("${file.chat-file-folder}")
    private String chatFileFolder;

    @Value("${file.chat-file-url-prefix}")
    private String chatFileUrlPrefix;


    // 저장할 실제 서버 파일 경로
    public String getProfileImagePath(String fileName) {
        // 항상 /로 끝나게 처리
        if (!profileImageFolder.endsWith("/")) {
            return profileImageFolder + "/" + fileName;
        }
        return profileImageFolder + fileName;
    }

    // 클라이언트에서 접근할 URL
    public String getProfileImageUrl(String fileName) {
        if (!profileImageUrlPrefix.endsWith("/")) {
            return profileImageUrlPrefix + "/" + fileName;
        }
        return profileImageUrlPrefix + fileName;
    }

    // 채팅 파일 저장 경로
    public String getChatFilePath(String fileName) {
        if (!chatFileFolder.endsWith("/")) {
            return chatFileFolder + "/" + fileName;
        }
        return chatFileFolder + fileName;
    }

    // 채팅 파일 접근 URL
    public String getChatFileUrl(String fileName) {
        if (!chatFileUrlPrefix.endsWith("/")) {
            return chatFileUrlPrefix + "/" + fileName;
        }
        return chatFileUrlPrefix + fileName;
    }
}