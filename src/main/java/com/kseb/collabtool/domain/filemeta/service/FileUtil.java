//package com.kseb.collabtool.domain.filemeta.service;
//
//import com.kseb.collabtool.global.exception.GeneralException;
//import com.kseb.collabtool.global.exception.Status;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.UUID;
//
// 이거 사용 못 함.
//public class FileUtil {
//
//    private static final String UPLOAD_DIR = "C:/Users/leech/Documents/GitHub/KSEB-backend/src/main/java/com/kseb/collabtool/utill/";
//
//    // 파일 저장 (경로 리턴) static???????????????? 진짜 이건 뭐임;;
//    public static String saveFile(MultipartFile file) {
//        try {
//            // 1. 디렉토리 없으면 생성
//            File uploadDir = new File(UPLOAD_DIR);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdirs();
//            }
//            String uuid = UUID.randomUUID().toString();
//            String originalName = file.getOriginalFilename();
//            String ext = "";
//            if (originalName != null && originalName.contains(".")) {
//                ext = originalName.substring(originalName.lastIndexOf('.'));
//            }
//            String saveName = uuid + ext;
//            File dest = new File(uploadDir, saveName);
//            file.transferTo(dest);
//            return dest.getAbsolutePath();
//        } catch (IOException e) {
//            throw new GeneralException(Status.FILE_NOT_SAVE, "파일 저장 실패: " + e.getMessage());
//        }
//    }
//
//    // 실제 파일 삭제
//    public static void deleteFile(String filePath) {
//        if (filePath == null) return;
//        File file = new File(filePath);
//        if (file.exists() && !file.delete()) {
//            throw new GeneralException(Status.INTERNAL_SERVER_ERROR, "파일 삭제 실패: " + filePath);
//        }
//    }
//}
