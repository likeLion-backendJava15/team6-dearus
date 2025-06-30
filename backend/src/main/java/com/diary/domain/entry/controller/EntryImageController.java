package com.diary.domain.entry.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class EntryImageController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile imageFile) {
        Map<String, String> result = new HashMap<>();

        try {
            // 파일명 생성 (UUID + 원래 이름)
            String originalFilename = imageFile.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;
            String tempDir = System.getProperty("user.dir") + "/src/main/resources/static/temp-uploads/";

            // 저장 경로
            File file = new File(tempDir + filename);
            file.getParentFile().mkdirs(); // 디렉토리 없으면 생성
            imageFile.transferTo(file);    // 파일 저장

            // 이미지 접근 URL
            result.put("url", "/temp-uploads/" + filename);

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "이미지 업로드 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
