package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.dao.ProductImageMapper;
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
                System.out.println("准备保存图片: productId=" + image.getProductId() + 
                                 ", url=" + image.getUrl() + 
                                 ", isMain=" + image.getIsMain() + 
                                 ", sort=" + image.getSort());
            }
            
            // 使用单条插入替代批量插入，以确保每条记录都能正确保存
            boolean allSuccess = true;
            for (ProductImage image : images) {
                try {
                    System.out.println("正在保存图片: " + image.getUrl());
                    int result = baseMapper.insert(image);
                    if (result <= 0) {
                        System.err.println("保存图片失败: " + image.getUrl());
                        allSuccess = false;
                    } else {
                        System.out.println("单张图片保存成功: " + image.getUrl());
                    }
                } catch (Exception e) {
                    System.err.println("保存图片异常: " + e.getMessage());
                    e.printStackTrace();
                    allSuccess = false;
                }
            }
            
            if (!allSuccess) {
                System.err.println("部分图片保存失败");
            }
            
            return allSuccess;
        } catch (Exception e) {
            System.err.println("批量保存图片失败: " + e.getMessage());
            e.printStackTrace();
            return false;
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