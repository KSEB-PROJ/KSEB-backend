package com.kseb.collabtool.domain.filemeta.service;

import com.kseb.collabtool.domain.filemeta.dto.FileResponse;
import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import com.kseb.collabtool.domain.filemeta.repository.FileRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException; //  IOException import
import java.nio.file.Files;   // Files import
import java.nio.file.Path;    // Path import
import java.nio.file.Paths;   // Paths import

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // FilePathUtil 주입.
    private final FilePathUtil filePathUtil;

    // ★ FileEntity → FileResponse로 변환해서 반환!
    @Transactional(readOnly = true)
    public FileResponse getFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(Status.FILE_NOT_FOUND, "파일을 찾을 수 없습니다."));
        return FileResponse.fromEntity(file);
    }

    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(Status.FILE_NOT_FOUND, "파일을 찾을 수 없습니다."));
        if (!file.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN, "본인이 업로드한 파일만 삭제할 수 있습니다.");
        }

        // FileUtil.deleteFile 대신 파일 직접 삭제
        try {
            // 1. URL에서 파일 이름을 추출.
            String fileNameWithUUID = file.getFileUrl().substring(file.getFileUrl().lastIndexOf("/") + 1);

            // 2. FilePathUtil을 사용해 실제 서버 경로를 얻어옴
            Path filePath = Paths.get(filePathUtil.getChatFilePath(fileNameWithUUID));

            // 3. 실제 파일을 삭제합니다.
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new GeneralException(Status.FILE_DELETE_FAILED, "물리적 파일 삭제에 실패했습니다: " + e.getMessage());
        }

        // DB에서 메타데이터를 삭제.
        fileRepository.deleteById(fileId);
    }
}