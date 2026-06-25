package com.goodskill.service.controller;

import com.goodskill.core.info.Result;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/oss/files")
public class OssFileController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + ext;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uniqueFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return Result.ok(uniqueFileName);
        } catch (Exception e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId,
            @RequestParam(value = "responseType", defaultValue = "url") String responseType) {
        if ("url".equals(responseType)) {
            try {
                String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileId)
                        .expiry(60 * 60 * 24)
                        .build());
                return ResponseEntity.ok(Result.ok(url));
            } catch (Exception e) {
                return ResponseEntity.ok(Result.fail("获取文件下载链接失败: " + e.getMessage()));
            }
        }

        // 返回二进制文件内容
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileId)
                .build())) {
            byte[] content = inputStream.readAllBytes();
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (fileId.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            } else if (fileId.endsWith(".jpg") || fileId.endsWith(".jpeg")) {
                contentType = MediaType.IMAGE_JPEG_VALUE;
            } else if (fileId.endsWith(".gif")) {
                contentType = MediaType.IMAGE_GIF_VALUE;
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileId + "\"")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail("文件读取失败: " + e.getMessage()));
        }
    }
}
