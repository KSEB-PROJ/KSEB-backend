package com.kseb.collabtool.domain.filemeta.repository;

import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFileUrl(String fileUrl);
}
