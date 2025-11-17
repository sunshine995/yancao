package com.office.yancao.controller;

import com.office.yancao.dto.FileData;
import com.office.yancao.dto.FileInfoRequest;
import com.office.yancao.dto.FileUploadResponse;
import com.office.yancao.untils.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload-path}")
    private String uploadDir;


    @Autowired
    private WebConfig webConfig;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new FileUploadResponse(false, "文件为空", null));
            }

            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + fileExtension;


            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 生成访问 URL
            String fileUrl = webConfig.getServerBaseUrl() + "/uploads/" + filename;


            FileData fileData = new FileData(
                    fileUrl, filename, originalFilename, file.getSize()
            );

            return ResponseEntity.ok(new FileUploadResponse(true, "文件上传成功", fileData));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new FileUploadResponse(false, "文件上传失败: " + e.getMessage(), null));
        }
    }

    // 保存文件信息接口
    @PostMapping("/save-file-info")
    public ResponseEntity<?> saveFileInfo(@RequestBody FileInfoRequest fileInfo) {
        // 这里可以保存文件信息到数据库
        System.out.println("保存文件信息: " + fileInfo);

        return ResponseEntity.ok().body("文件信息保存成功");
    }
}
