package com.secondtrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 输出绝对路径用于调试
        System.out.println("上传目录绝对路径: " + dir.getAbsolutePath());
        
        // 注册上传目录的静态资源访问路径 - 确保路径以斜杠结尾
        String resourceLocation = "file:" + dir.getAbsolutePath() + File.separator;
        // 替换Windows的反斜杠为正斜杠
        resourceLocation = resourceLocation.replace("\\", "/");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
        
        System.out.println("已配置静态资源映射: /uploads/** -> " + resourceLocation);
    }
} 