package com.teamcrazyperformance.blockitserver.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String storeFile(MultipartFile file, String userId);
    byte[] loadFileAsBytes(String fileNamee);
}
