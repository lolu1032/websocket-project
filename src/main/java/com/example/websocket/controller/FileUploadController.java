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

    /**
     * 업로드된 파일을 저장할 디렉토리의 경로를 정의합니다.
     */
    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    /**
     * @RequestParam("file") MultipartFile file: 클라이언트가 업로드하는 파일을 MultipartFile 객체로 받습니다.
     */
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
        }

        try {
            // 저장할 파일 경로 생성
            /**
             * 업로드된 파일의 원래 이름을 가져오고 저장할 경로를 생성
             */
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 파일 저장
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.write(filePath, file.getBytes());

            // URL 인코딩
            /**
             * 파일 이름을 URL 인코딩하여 파일 URL을 생성하고, 파일 이름과 URL을 포함하는 FileUploadResponse 객체를 응답으로 반환합니다.
             */
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8"); // 파일 이름 인코딩
            String fileUrl = "/files/uploads/" + encodedFileName; // 인코딩된 파일 이름 사용

            return ResponseEntity.ok().body(new FileUploadResponse(fileName, fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류 발생");
        }
    }

    /**
     * 파일 다운로드 메소드
     * @param fileName
     * @return
     */
    @GetMapping("/uploads/{fileName:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        try {
            /**
             * 파일 읽기 및 응답
             * 지정된 경로에서 파일 데이터를 읽고, HTTP 응답 헤더에 파일 타입을 설정한 후 파일 데이터를 포함하여 200 OK 응답을 반환합니다.
             */
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            /**
             * 바이트 파일을 data에 저장한다.
             */
            byte[] data = Files.readAllBytes(filePath);
            /**
             * HttpHeaders 객체 생성
             * HTTP 응답 또는 요청에 사용할 수 있는 헤더 정보를 담기 위한 클래스입니다.
             */
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(filePath)); // 파일 타입 설정

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 파일 업로드 응답 클래스
    static class FileUploadResponse {
        /**
         * 파일 원본 이름 저장하는 문자열
         */
        private String fileName;
        /**
         * 파일이 서버에 저장된 후 접근할 수 있는 URL을 저장하는 문자열
         */
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
