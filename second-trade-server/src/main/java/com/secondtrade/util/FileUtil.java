package com.secondtrade.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {
    
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件为空");
        }
        
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            throw new IOException("上传目录未配置");
        }
        
        try {
            System.out.println("开始上传文件: " + file.getOriginalFilename());
            System.out.println("上传目录: " + uploadDir);
            
            // 创建上传目录
            File dirFile = new File(uploadDir);
            Path dirPath = dirFile.toPath();
            
            if (!dirFile.exists()) {
                System.out.println("目录不存在，正在创建目录: " + uploadDir);
                try {
                    boolean created = dirFile.mkdirs();
                    if (!created) {
                        throw new IOException("无法创建目录: " + uploadDir);
                    }
                    System.out.println("目录创建成功: " + uploadDir);
                } catch (Exception e) {
                    System.err.println("创建目录失败: " + e.getMessage());
                    throw new IOException("创建目录失败: " + e.getMessage(), e);
                }
            }
            
            // 检查目录是否可写
            if (!dirFile.canWrite()) {
                System.err.println("目录不可写: " + uploadDir);
                throw new IOException("目录不可写: " + uploadDir);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unknown_file";
            }
            
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }
            
            String filename = UUID.randomUUID().toString() + extension;
            
            // 构建完整路径
            Path fullPath = dirPath.resolve(filename);
            File targetFile = fullPath.toFile();
            System.out.println("文件将被保存到: " + fullPath.toString());
            
            // 保存文件
            try {
                file.transferTo(targetFile);
            } catch (Exception e) {
                System.err.println("保存文件失败: " + e.getMessage());
                throw new IOException("保存文件失败: " + e.getMessage(), e);
            }
            
            if (!targetFile.exists()) {
                throw new IOException("文件写入失败，目标文件不存在: " + targetFile.getAbsolutePath());
            }
            
            System.out.println("文件上传成功: " + filename);
            return filename;
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("文件上传失败: " + e.getMessage(), e);
        }
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