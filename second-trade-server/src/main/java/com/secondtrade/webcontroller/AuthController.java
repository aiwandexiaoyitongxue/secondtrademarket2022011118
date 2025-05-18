package com.secondtrade.webcontroller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.secondtrade.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController extends BaseController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_PNG_VALUE)
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
    public Result<Void> verifyCaptcha(@RequestBody Map<String, String> data, HttpSession session) {
        String captcha = data.get("captcha");
        String sessionCaptcha = (String) session.getAttribute("captcha");
        
        if (captcha == null || captcha.trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        
        if (sessionCaptcha == null) {
            return Result.error("验证码已过期");
        }
        
        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            return Result.error("验证码错误");
        }
        
        // 验证成功后清除session中的验证码
        session.removeAttribute("captcha");
        
        return Result.success(null);
    }
}