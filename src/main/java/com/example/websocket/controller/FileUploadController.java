package com.example.websocket.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
        }

        try {
            // 저장할 파일 경로 생성
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 파일 저장
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.write(filePath, file.getBytes());

            // URL 인코딩
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8"); // 파일 이름 인코딩
            String fileUrl = "/files/uploads/" + encodedFileName; // 인코딩된 파일 이름 사용

            return ResponseEntity.ok().body(new FileUploadResponse(fileName, fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류 발생");
        }
    }

    @GetMapping("/uploads/{fileName:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            byte[] data = Files.readAllBytes(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(filePath)); // 파일 타입 설정

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 파일 업로드 응답 클래스
    static class FileUploadResponse {
        private String fileName;
        private String fileUrl;

        public FileUploadResponse(String fileName, String fileUrl) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }
}
