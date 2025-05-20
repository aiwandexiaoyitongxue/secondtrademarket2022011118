package com.secondtrade.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;    
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin


public class AuthController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @GetMapping("/captcha")
    public ResponseEntity<byte[]> getCaptcha(HttpSession session) {
        try {
            // 生成验证码文本
            String text = defaultKaptcha.createText();
            // 生成验证码图片
            BufferedImage image = defaultKaptcha.createImage(text);
            
            // 将验证码存入session
            session.setAttribute("captcha", text);
            
            // 将图片转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            
            return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    @PostMapping("/verify-captcha")
    public ResponseEntity<?> verifyCaptcha(@RequestBody Map<String, String> data, HttpSession session) {
        String captcha = data.get("captcha");
        String sessionCaptcha = (String) session.getAttribute("captcha");
        
        Map<String, Object> response = new HashMap<>();
        
        if (captcha == null || captcha.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "验证码不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (sessionCaptcha == null) {
            response.put("success", false);
            response.put("message", "验证码已过期");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            response.put("success", false);
            response.put("message", "验证码错误");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 验证成功后清除session中的验证码
        session.removeAttribute("captcha");
        
        response.put("success", true);
        response.put("message", "验证码正确");
        return ResponseEntity.ok(response);
    }
}