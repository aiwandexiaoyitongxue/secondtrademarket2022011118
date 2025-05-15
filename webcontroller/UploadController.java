package com.secondtrade.webcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/upload")
@CrossOrigin // 允许跨域
public class UploadController {

    @PostMapping                    
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 这里可以保存文件到本地或云存储
            // String fileName = file.getOriginalFilename();
            // file.transferTo(new File("你的保存路径/" + fileName));

            // 简单返回成功
            return ResponseEntity.ok().body("上传成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("上传失败: " + e.getMessage());
        }
    }
}