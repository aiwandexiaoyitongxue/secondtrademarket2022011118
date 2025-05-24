package com.secondtrade.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {
    
    public static String uploadFile(MultipartFile file, String baseUploadDir, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件为空");
        }
        
        if (baseUploadDir == null || baseUploadDir.trim().isEmpty()) {
            throw new IOException("基础上传目录未配置");
        }
        
        try {
            System.out.println("开始上传文件: " + file.getOriginalFilename());
            System.out.println("基础上传目录: " + baseUploadDir);
            System.out.println("子目录: " + (subDir == null || subDir.isEmpty() ? "无" : subDir));
            
            // 构建完整的物理上传目录路径 (基础目录 + 子目录)
            Path uploadDirPath;
            if (subDir == null || subDir.isEmpty()) {
                uploadDirPath = Paths.get(baseUploadDir); // 没有子目录，直接使用基础目录
            } else {
                uploadDirPath = Paths.get(baseUploadDir, subDir); // 有子目录，拼接
            }
            
            File dirFile = uploadDirPath.toFile();
            
            if (!dirFile.exists()) {
                System.out.println("目录不存在，正在创建目录: " + uploadDirPath.toString());
                try {
                    boolean created = dirFile.mkdirs();
                    if (!created) {
                        System.err.println("无法创建目录: " + uploadDirPath.toString());
                        throw new IOException("无法创建目录: " + uploadDirPath.toString());
                    }
                    System.out.println("目录创建成功: " + uploadDirPath.toString());
                } catch (Exception e) {
                    System.err.println("创建目录失败: " + e.getMessage());
                    throw new IOException("创建目录失败: " + e.getMessage(), e);
                }
            }
            
            // 检查目录是否可写
            if (!dirFile.canWrite()) {
                System.err.println("目录不可写: " + uploadDirPath.toString());
                throw new IOException("目录不可写: " + uploadDirPath.toString());
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
            
            // 构建完整的物理保存路径
            Path fullPath = uploadDirPath.resolve(filename); // 将上传目录和文件名正确拼接
            File targetFile = fullPath.toFile();
            System.out.println("文件将被物理保存到: " + targetFile.getAbsolutePath()); // 打印完整的物理路径
            
            // 保存文件到物理路径
            try {
                file.transferTo(targetFile);
            } catch (Exception e) {
                System.err.println("保存文件失败: " + e.getMessage());
                throw new IOException("保存文件失败: " + e.getMessage(), e);
            }
            
            if (!targetFile.exists()) {
                System.err.println("文件写入失败，目标文件不存在: " + targetFile.getAbsolutePath());
                throw new IOException("文件写入失败，目标文件不存在: " + targetFile.getAbsolutePath());
            }
            
            // 返回前端可访问的URL片段
            // URL片段应该包含子目录 (如果存在)
            String urlFragment;
            if (subDir == null || subDir.isEmpty()) {
                 // 如果没有子目录，URL片段就是 /static/upload/ + filename
                 urlFragment = "/static/upload/" + filename;
            } else {
                // 如果有子目录，URL片段就是 /static/upload/ + subDir + / + filename
                 // 确保 subDir 没有前导或尾随的斜杠
                String cleanedSubDir = subDir.replace("\\", "/").replaceAll("^[\\/]+|[\\/]+$", "");
                urlFragment = "/static/upload/" + cleanedSubDir + "/" + filename;
            }
           
            System.out.println("文件物理保存成功，返回数据库存储的URL片段: " + urlFragment); // 打印返回的值
            return urlFragment;
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Deprecated
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        System.out.println("警告: 正在使用 FileUtil.uploadFile(file, uploadDir) 的旧版本, 建议使用带有 subDir 的版本.");
        // 这里简单地将 uploadDir 作为 baseUploadDir，子目录为空
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