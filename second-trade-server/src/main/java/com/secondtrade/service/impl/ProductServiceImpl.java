package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.mapper.ProductMapper;
import com.secondtrade.service.ProductService;
import com.secondtrade.service.ProductImageService;
import com.secondtrade.service.MerchantService;
import com.secondtrade.service.CategoryService;
import com.secondtrade.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CategoryService categoryService;

    @PostConstruct
    public void init() {
        System.out.println("ProductServiceImpl initialized");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishProduct(String productJson, List<MultipartFile> images) throws JsonProcessingException {
        try {
            System.out.println("接收到的商品数据: " + productJson);
            // 解析商品数据
            Product product = objectMapper.readValue(productJson, Product.class);
            System.out.println("解析后的商品对象: " + product);
            
            // 验证商家是否存在
            if (!validateMerchant(product.getMerchantId())) {
                throw new RuntimeException("商家不存在，ID: " + product.getMerchantId());
            }
            
            // 验证分类是否存在
            if (!validateCategory(product.getCategoryId())) {
                throw new RuntimeException("商品分类不存在，ID: " + product.getCategoryId());
            }
            
            // 设置商品状态为待审核
            product.setStatus(0);
            product.setSales(0);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedTime(now);
            product.setUpdatedTime(now);
            
            // 设置删除标记
            product.setDeleted(0);
            
            // 验证必要字段
            if (product.getMerchantId() == null) {
                throw new RuntimeException("商家ID不能为空");
            }
            if (product.getCategoryId() == null) {
                throw new RuntimeException("商品类目不能为空");
            }
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new RuntimeException("商品名称不能为空");
            }
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("商品价格必须大于0");
            }
            
            System.out.println("准备保存商品信息: " + product);
            // 保存商品信息
            boolean saveResult = save(product);
            System.out.println("商品信息保存结果: " + saveResult);
            
            if (!saveResult) {
                throw new RuntimeException("保存商品信息失败");
            }
            
            // 处理商品图片
            if (images != null && !images.isEmpty()) {
                System.out.println("开始处理商品图片，图片数量: " + images.size());
                List<ProductImage> productImages = images.stream()
                    .map(file -> {
                        try {
                            String url = FileUtil.uploadFile(file, uploadDir);
                            ProductImage image = new ProductImage();
                            image.setProductId(product.getId());
                            image.setUrl(url);
                            image.setIsMain(images.indexOf(file) == 0);
                            return image;
                        } catch (Exception e) {
                            System.err.println("上传图片失败: " + e.getMessage());
                            throw new RuntimeException("上传图片失败", e);
                        }
                    })
                    .collect(Collectors.toList());
                
                boolean batchSaveResult = productImageService.saveBatch(productImages);
                if (!batchSaveResult) {
                    throw new RuntimeException("保存商品图片失败");
                }
                System.out.println("商品图片保存完成");
            }
        } catch (Exception e) {
            System.err.println("发布商品失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Product> getProductList(Long categoryId, String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Product::getCategoryId, categoryId)
               .like(keyword != null, Product::getName, keyword)
               .orderByDesc(Product::getCreatedTime);
        
        Page<Product> pageParam = new Page<>(page, size);
        return page(pageParam, wrapper).getRecords();
    }

    @Override
    public Product getProductDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void updateProduct(Long id, Product product) {
        product.setId(id);
        updateById(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        removeById(id);
        // 删除商品相关的图片
        productImageService.removeByProductId(id);
    }

    private boolean validateMerchant(Long merchantId) {
        if (merchantId == null) {
            return false;
        }
        return merchantService.getById(merchantId) != null;
    }
    
    private boolean validateCategory(Long categoryId) {
        if (categoryId == null) {
            return false;
        }
        return categoryService.getById(categoryId) != null;
    }
} 