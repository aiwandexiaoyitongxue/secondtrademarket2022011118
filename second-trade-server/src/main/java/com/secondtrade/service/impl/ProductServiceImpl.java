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

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        System.out.println("上传目录配置: " + uploadDir);
        
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            System.err.println("警告: 上传目录未配置，将使用默认目录");
            uploadDir = System.getProperty("user.dir") + "/uploads";
        }
        
        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("创建上传目录结果: " + (created ? "成功" : "失败"));
            
            if (!created) {
                System.err.println("无法创建上传目录，将尝试创建临时目录");
                try {
                    dir = new File(System.getProperty("java.io.tmpdir"), "second-trade-uploads");
                    if (!dir.exists()) {
                        created = dir.mkdirs();
                    }
                    uploadDir = dir.getAbsolutePath();
                    System.out.println("使用临时目录作为上传目录: " + uploadDir);
                } catch (Exception e) {
                    System.err.println("创建临时上传目录失败: " + e.getMessage());
                }
            }
        }
        
        // 检查目录是否可写
        if (!dir.canWrite()) {
            System.err.println("警告: 上传目录不可写: " + uploadDir);
        }
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
                
                // 使用ArrayList代替stream，便于错误处理
                List<ProductImage> productImages = new ArrayList<>();
                
                for (int i = 0; i < images.size(); i++) {
                    MultipartFile file = images.get(i);
                    
                    try {
                        if (file.isEmpty()) {
                            System.out.println("跳过空文件");
                            continue;
                        }
                        
                        System.out.println("处理第" + (i+1) + "张图片: " + file.getOriginalFilename() + ", 大小: " + file.getSize() + "字节");
                        String url = FileUtil.uploadFile(file, uploadDir);
                        
                        ProductImage image = new ProductImage();
                        image.setProductId(product.getId());
                        image.setUrl(url);
                        image.setIsMain(i == 0); // 第一张为主图
                        image.setCreatedTime(now);
                        image.setUpdatedTime(now);
                        image.setDeleted(0);
                        
                        productImages.add(image);
                        System.out.println("图片上传成功: " + url);
                    } catch (Exception e) {
                        System.err.println("处理图片失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                if (productImages.isEmpty()) {
                    throw new RuntimeException("没有成功上传的图片");
                }
                
                System.out.println("准备保存" + productImages.size() + "张商品图片");
                boolean batchSaveResult = productImageService.saveBatch(productImages);
                
                if (!batchSaveResult) {
                    throw new RuntimeException("保存商品图片失败");
                }
                System.out.println("商品图片保存完成");
            } else {
                System.out.println("没有图片需要处理");
            }
        } catch (Exception e) {
            System.err.println("发布商品失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Product> getProductList(Long categoryId, String keyword, Long merchantId, Integer status, Integer excludeStatus, Integer page, Integer size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Product::getCategoryId, categoryId)
               .eq(merchantId != null, Product::getMerchantId, merchantId)
               .eq(status != null, Product::getStatus, status)
               .ne(excludeStatus != null, Product::getStatus, excludeStatus)
               .like(keyword != null && !keyword.isEmpty(), Product::getName, keyword)
               .orderByDesc(Product::getCreatedTime);
        
        Page<Product> pageParam = new Page<>(page, size);
        List<Product> products = page(pageParam, wrapper).getRecords();
        
        // 为每个商品获取图片信息
        for (Product product : products) {
            List<ProductImage> images = productImageService.findByProductId(product.getId());
            product.setProductImages(images);
        }
        
        return products;
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        try {
            System.out.println("开始删除商品，ID: " + id);
            
            // 检查商品是否存在
            Product product = getById(id);
            if (product == null) {
                System.err.println("要删除的商品不存在，ID: " + id);
                throw new RuntimeException("商品不存在，ID: " + id);
            }
            
            System.out.println("商品信息: " + product);
            
            // 先删除商品相关的图片
            try {
                System.out.println("删除商品关联的图片，商品ID: " + id);
                productImageService.removeByProductId(id);
                System.out.println("商品图片删除成功");
            } catch (Exception e) {
                System.err.println("删除商品图片失败: " + e.getMessage());
                e.printStackTrace();
                // 继续删除商品，不因图片删除失败而中断
            }
            
            // 设置更新时间
            product.setUpdatedTime(LocalDateTime.now());
            
            // 删除商品记录
            boolean result = removeById(id);
            System.out.println("商品记录删除结果: " + (result ? "成功" : "失败"));
            
            if (!result) {
                throw new RuntimeException("删除商品记录失败，ID: " + id);
            }
            
            System.out.println("商品删除完成，ID: " + id);
        } catch (Exception e) {
            System.err.println("删除商品异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private boolean validateMerchant(Long merchantId) {
        if (merchantId == null) {
            System.out.println("验证失败：商家ID为空");
            return false;
        }
        
        boolean exists = merchantService.getById(merchantId) != null;
        System.out.println("验证商家ID " + merchantId + ": " + (exists ? "存在" : "不存在"));
        return exists;
    }
    
    private boolean validateCategory(Long categoryId) {
        if (categoryId == null) {
            System.out.println("验证失败：分类ID为空");
            return false;
        }
        
        boolean exists = categoryService.getById(categoryId) != null;
        System.out.println("验证分类ID " + categoryId + ": " + (exists ? "存在" : "不存在"));
        return exists;
    }
} 