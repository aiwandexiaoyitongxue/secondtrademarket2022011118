package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.mapper.ProductImageMapper;
import com.secondtrade.service.ProductImageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {
    
    @Override
    public List<ProductImage> findByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        return list(wrapper);
    }

    @Override
    public ProductImage saveImage(ProductImage productImage) {
        baseMapper.insert(productImage);
        return productImage;
    }

    @Override
    public void deleteByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        remove(wrapper);
    }

    @Override
    public boolean saveBatch(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            System.out.println("没有图片需要保存");
            return true;
        }
        
        try {
            System.out.println("开始批量保存" + images.size() + "张商品图片");
            for (ProductImage image : images) {
                System.out.println("保存图片: productId=" + image.getProductId() + ", url=" + image.getUrl() + ", isMain=" + image.getIsMain());
            }
            
            int result = baseMapper.insertBatch(images);
            System.out.println("批量保存结果: 成功保存" + result + "张图片");
            return result > 0;
        } catch (Exception e) {
            System.err.println("批量保存图片失败: " + e.getMessage());
            e.printStackTrace();
            // 单张插入尝试
            System.out.println("尝试单张插入...");
            boolean allSuccess = true;
            for (ProductImage image : images) {
                try {
                    baseMapper.insert(image);
                    System.out.println("单张插入成功: " + image.getUrl());
                } catch (Exception ex) {
                    System.err.println("单张插入失败: " + ex.getMessage());
                    allSuccess = false;
                }
            }
            return allSuccess;
        }
    }

    @Override
    public void removeByProductId(Long productId) {
        if (productId == null) {
            System.err.println("删除图片的商品ID为空");
            return;
        }

        try {
            // 先查找商品下的所有图片
            System.out.println("查询商品图片，商品ID: " + productId);
            LambdaQueryWrapper<ProductImage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductImage::getProductId, productId);
            
            List<ProductImage> images = list(queryWrapper);
            System.out.println("找到图片数量: " + (images != null ? images.size() : 0));
            
            if (images != null && !images.isEmpty()) {
                // 删除物理文件（可选，取决于业务需求）
                // 这里先不删除物理文件，以免误删其他商品引用的图片
                
                // 删除数据库记录
                System.out.println("执行删除操作，删除" + images.size() + "条图片记录");
                LambdaQueryWrapper<ProductImage> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(ProductImage::getProductId, productId);
                
                boolean success = remove(deleteWrapper);
                System.out.println("删除图片记录结果: " + (success ? "成功" : "失败"));
            } else {
                System.out.println("商品没有关联图片，无需删除");
            }
        } catch (Exception e) {
            System.err.println("删除商品图片异常: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常，让调用方处理
        }
    }
} 