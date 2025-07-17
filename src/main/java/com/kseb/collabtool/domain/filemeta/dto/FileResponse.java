package com.kseb.collabtool.domain.filemeta.dto;

import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
    private Long id;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String mimeType;

    public static FileResponse fromEntity(FileEntity file) {
        return FileResponse.builder()
                .id(file.getId())
                .fileUrl(file.getFileUrl())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .mimeType(file.getMimeType())
                .build();
    }
}
