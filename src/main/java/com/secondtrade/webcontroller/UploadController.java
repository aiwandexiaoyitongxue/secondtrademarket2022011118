package com.secondtrade.webcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;  // 添加这个导入
import java.util.Map;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    private static final String UPLOAD_DIR = "C:\\Users\\xiaoyitongxue\\Pictures\\Screenshots\\";

    @PostMapping                    
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "请选择文件"
                ));
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String fileName = UUID.randomUUID().toString() + "." + fileType;
            
            // 使用固定目录
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            
            // 返回可访问的URL路径
            String url = "/static/upload/" + fileName;
            return ResponseEntity.ok().body(Map.of(
                "code", 200,
                "url", url,
                "name", originalFilename,
                "status", "success"
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