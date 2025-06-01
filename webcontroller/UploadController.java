package com.secondtrade.webcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.io.File;
import java.util.UUID;
import com.secondtrade.util.FileUtil;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/public")
    public ResponseEntity<?> publicUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "请选择文件"
                ));
            }

            // 使用 FileUtil 上传文件
            String url = FileUtil.uploadFile(file, uploadPath, "public");
            
            return ResponseEntity.ok().body(Map.of(
                "code", 200,
                "data", Map.of(
                    "url", url
                ),
                "message", "上传成功"
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "上传失败: " + e.getMessage()
            ));
        }
    }
}