package com.secondtrade.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {
    
    public static String uploadFile(MultipartFile file, String baseDir, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        
        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("文件名不能为空");
            }
            
            System.out.println("开始上传文件: " + originalFilename);
            System.out.println("文件大小: " + file.getSize() + " bytes");
            System.out.println("文件类型: " + file.getContentType());
            
            // 生成新的文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString().replaceAll("-", "") + extension;
            
            // 构建完整的保存路径
            String fullDir = baseDir + File.separator + subDir;
            File dir = new File(fullDir);
            if (!dir.exists()) {
                System.out.println("创建目录: " + fullDir);
                if (!dir.mkdirs()) {
                    throw new RuntimeException("创建目录失败: " + fullDir);
                }
            }
            
            // 构建完整的文件路径
            String fullPath = fullDir + File.separator + newFilename;
            System.out.println("文件将保存到: " + fullPath);
            
            // 保存文件
            File dest = new File(fullPath);
            file.transferTo(dest);
            System.out.println("文件保存成功");
            
            // 返回访问URL
            String url = "/allimage/" + subDir + "/" + newFilename;
            System.out.println("文件访问URL: " + url);
            return url;
            
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Deprecated
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        System.out.println("警告: 正在使用 FileUtil.uploadFile(file, uploadDir) 的旧版本, 建议使用带有 subDir 的版本.");
        return uploadFile(file, uploadDir, "");
    }
    
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("删除文件 " + filePath + ": " + (deleted ? "成功" : "失败"));
            } else {
                System.out.println("文件不存在，无需删除: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("删除文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 