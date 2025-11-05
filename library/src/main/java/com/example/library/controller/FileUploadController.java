package com.example.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/uploads")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        // Lấy đường dẫn tuyệt đối tới thư mục uploads
        Path root = Paths.get(System.getProperty("user.dir")).resolve(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root); // tạo nếu chưa có

        // Làm sạch tên file và tránh trùng
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot != -1) ext = original.substring(dot);
        String filename = java.util.UUID.randomUUID() + ext;

        Path target = root.resolve(filename);
        // KHÔNG dùng file.transferTo(String tương đối); dùng Files.copy để kiểm soát đường dẫn tuyệt đối
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL public (khớp với resource handler ở phần B)
        String publicUrl = "/uploads/" + filename;
        return ResponseEntity.ok(Map.of("url", publicUrl));
    }
}
