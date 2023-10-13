package com.teamcrazyperformance.blockitserver.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;

@Slf4j
@Service
public class UserProfileImgService implements FileService {
    @Value("${file.upload-dir}")
    private String uploadDirectory; // application.properties에서 경로 설정

    /* 파일 저장 및 파일명 반환 */
    @Override
    public String storeFile(MultipartFile file, String userId) {
        try {
            // 파일명 정의
            String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";

            Path filePath = Paths.get(uploadDirectory + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Firebase GID '" + userId + "의 프로필 사진 저장 성공");

            return fileName;
        } catch (Exception ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try later!", ex);
        }
    }

    /* 파일을 byte[] 배열 형태로 불러오기 */
    @Override
    public byte[] loadFileAsBytes(String fileName) {
        try {
            Path filePath = Paths.get(uploadDirectory + fileName);
            return Files.readAllBytes(filePath);
        } catch (Exception ex) {
            throw new RuntimeException("Could not load file " + fileName + ". Please try again!", ex);
        }
    }
}
