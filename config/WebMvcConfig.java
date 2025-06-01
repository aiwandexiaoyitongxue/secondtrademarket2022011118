package com.secondtrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            System.out.println("创建上传目录: " + uploadDir.getAbsolutePath());
            uploadDir.mkdirs();
        }
        System.out.println("上传目录绝对路径: " + uploadDir.getAbsolutePath());

        // 商品图片 - 修改为正确的路径映射
        registry.addResourceHandler("/static/upload/**")
                .addResourceLocations("file:" + uploadPath + "/publishimage/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // 添加发布图片映射
        registry.addResourceHandler("/api/allimage/publishimage/**")
                .addResourceLocations("file:" + uploadPath + "/publishimage/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // 添加直接访问图片的映射
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + uploadPath + "/publishimage/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // 头像图片
        registry.addResourceHandler("/static/avatar/**")
                .addResourceLocations("file:" + uploadPath + "/avatar/")
                .setCachePeriod(3600)
                .resourceChain(true);

        // 其它静态资源
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 前端开发服务器
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}