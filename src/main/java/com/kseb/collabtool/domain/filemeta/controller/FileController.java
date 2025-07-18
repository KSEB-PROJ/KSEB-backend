package com.kseb.collabtool.domain.filemeta.controller;

import com.kseb.collabtool.domain.filemeta.dto.FileResponse;
import com.kseb.collabtool.domain.filemeta.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    // 파일 메타정보 조회
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFile(@PathVariable Long fileId) {
        FileResponse res = fileService.getFile(fileId);
        return ResponseEntity.ok(res);
    }

    // 파일 삭제 (본인 업로드 파일만)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        fileService.deleteFile(fileId, userId);
        return ResponseEntity.ok().build();
    }
}
