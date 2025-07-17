package com.kseb.collabtool.domain.filemeta.service;

import com.kseb.collabtool.domain.filemeta.dto.FileResponse;
import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import com.kseb.collabtool.domain.filemeta.repository.FileRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.domain.filemeta.service.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // ★ FileEntity → FileResponse로 변환해서 반환!
    @Transactional(readOnly = true)
    public FileResponse getFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(Status.FILE_NOT_FOUND, "파일을 찾을 수 없습니다."));
        return FileResponse.fromEntity(file); // 여기!
    }

    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(Status.FILE_NOT_FOUND, "파일을 찾을 수 없습니다."));
        if (!file.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN, "본인이 업로드한 파일만 삭제할 수 있습니다.");
        }

        // 실제 파일도 삭제 (FileUtil 사용)
        FileUtil.deleteFile(file.getFileUrl());
        // DB에서 메타 삭제
        fileRepository.deleteById(fileId);
    }
}
