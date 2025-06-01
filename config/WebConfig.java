package com.secondtrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.path}")
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
        
        // 商品图片目录映射
        registry.addResourceHandler("/static/upload/images/**")
                .addResourceLocations("file:" + uploadDir + "/products/");

        // 头像目录映射
        registry.addResourceHandler("/static/upload/avatar/**")
                .addResourceLocations("file:" + uploadDir + "/avatar/");
    }
}