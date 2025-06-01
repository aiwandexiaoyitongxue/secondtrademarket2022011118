package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.dao.ProductMapper;
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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.secondtrade.entity.ProductReview;
import com.secondtrade.dao.ProductReviewDao;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${upload.path}")
    private String baseUploadDir;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @Autowired
    private MerchantService merchantService;
    @Override
    public List<Product> getProductList(Long categoryId, String keyword, Long merchantId,
                                    Integer status, Integer excludeStatus, Integer page, Integer size) {
        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        int offset = (page - 1) * size;
        
        System.out.println("ProductServiceImpl.getProductList 开始查询，参数：");
        System.out.println("categoryId=" + categoryId);
        System.out.println("keyword=" + keyword);
        System.out.println("merchantId=" + merchantId);
        System.out.println("status=" + status);
        System.out.println("excludeStatus=" + excludeStatus);
        System.out.println("page=" + page);
        System.out.println("size=" + size);
        System.out.println("offset=" + offset);
        
        List<Product> products = baseMapper.selectProductListWithImage(categoryId, keyword, merchantId, status, excludeStatus, size, offset);
        
        System.out.println("查询到 " + products.size() + " 个商品");
        for (Product p : products) {
            System.out.println("商品详情: ID=" + p.getId() + 
                             ", 名称=" + p.getName() + 
                             ", 类别ID=" + p.getCategoryId() + 
                             ", 描述=" + p.getDescription() + 
                             ", 商家ID=" + p.getMerchantId() + 
                             ", 价格=" + p.getPrice() + 
                             ", 库存=" + p.getStock() + 
                             ", 图片=" + p.getImage());
        }
        return products;
    }
   // 添加缺失的方法
    @Override
    public Product getProductDetail(Long id) {
        return baseMapper.selectByIdWithImage(id);
    }
  @Override
    public Map<String, Object> getAllProducts(Long categoryId, Integer status, Integer excludeStatus, int page, int size) {
        List<Product> products = baseMapper.selectProductsWithFilters(categoryId, null, null, status, excludeStatus);
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", products.size()); // 你可以用分页插件获取真实total
        return result;
    }
    @Autowired
    private CategoryService categoryService;
        @Transactional(rollbackFor = Exception.class)
    @Override
    public void publishProduct(String productJson, List<MultipartFile> images, Long merchantId) throws Exception {
        try {
            System.out.println("=== 开始发布商品 ===");
            System.out.println("接收到的数据：");
            System.out.println("商品JSON: " + productJson);
            System.out.println("图片数量: " + (images != null ? images.size() : 0));
            System.out.println("商家ID: " + merchantId);

            // 1. 解析商品数据
            Product product = null;
            try {
                product = objectMapper.readValue(productJson, Product.class);
                System.out.println("商品数据解析成功: " + product);
            } catch (Exception e) {
                System.err.println("解析商品JSON失败: " + e.getMessage());
                throw new RuntimeException("商品数据格式错误: " + e.getMessage());
            }

            // 2. 校验商家和分类
            if (merchantId == null) {
                throw new RuntimeException("商家ID不能为空");
            }
            if (!validateMerchant(merchantId)) {
                throw new RuntimeException("商家不存在，ID: " + merchantId);
            }
            if (product.getCategoryId() == null) {
                throw new RuntimeException("商品类目不能为空");
            }
            if (!validateCategory(product.getCategoryId())) {
                throw new RuntimeException("商品分类不存在，ID: " + product.getCategoryId());
            }

            // 3. 校验商品必填字段
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new RuntimeException("商品名称不能为空");
            }
            if (product.getPrice() == null || product.getPrice() <= 0) {
                throw new RuntimeException("商品价格必须大于0");
            }
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new RuntimeException("商品库存必须大于0");
            }

            // 4. 设置商品属性
            product.setMerchantId(merchantId);
            product.setStatus(0); // 待审核
            product.setSales(0);
            Date now = new Date();
            product.setCreatedTime(now);
            product.setUpdatedTime(now);
            product.setDeleted(0);

            // 5. 保存商品主信息
            try {
                System.out.println("准备保存商品信息: " + product);
                boolean saveResult = save(product);
                System.out.println("商品信息保存结果: " + saveResult);
                if (!saveResult) {
                    throw new RuntimeException("保存商品信息失败");
                }
                System.out.println("商品保存成功，ID: " + product.getId());
            } catch (Exception e) {
                System.err.println("保存商品信息时发生错误: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("保存商品信息失败: " + e.getMessage());
            }

            // 6. 处理商品图片
            if (images != null && !images.isEmpty()) {
                List<ProductImage> productImages = new ArrayList<>();
                for (int i = 0; i < images.size(); i++) {
                    MultipartFile image = images.get(i);
                    if (image != null && !image.isEmpty()) {
                        try {
                            System.out.println("开始处理第" + (i + 1) + "张图片: " + image.getOriginalFilename());
                            System.out.println("图片大小: " + image.getSize() + " bytes");
                            System.out.println("图片类型: " + image.getContentType());
                            
                            // 检查图片大小
                            if (image.getSize() > 10 * 1024 * 1024) { // 10MB
                                throw new RuntimeException("图片大小不能超过10MB");
                            }
                            
                            // 检查图片类型
                            String contentType = image.getContentType();
                            if (contentType == null || !contentType.startsWith("image/")) {
                                throw new RuntimeException("只能上传图片文件");
                            }
                            
                            // 上传图片
                            System.out.println("开始上传图片到目录: " + baseUploadDir);
                            String imageUrl = FileUtil.uploadFile(image, baseUploadDir, "publishimage");
                            System.out.println("图片上传成功，URL: " + imageUrl);
                            
                            ProductImage productImage = new ProductImage();
                            productImage.setProductId(product.getId());
                            productImage.setUrl(imageUrl);
                            productImage.setSort(i);
                            productImage.setIsMain(i == 0 ? 1 : 0); // 第一张图片设为主图
                            productImage.setCreatedTime(now);
                            productImage.setUpdatedTime(now);
                            productImage.setDeleted(0);
                            
                            productImages.add(productImage);
                            System.out.println("图片信息已添加到列表，当前列表大小: " + productImages.size());
                        } catch (Exception e) {
                            System.err.println("处理第" + (i + 1) + "张图片失败: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("处理商品图片失败: " + e.getMessage());
                        }
                    }
                }
                
                // 批量保存图片信息
                if (!productImages.isEmpty()) {
                    try {
                        System.out.println("准备批量保存" + productImages.size() + "张图片信息到数据库");
                        for (ProductImage img : productImages) {
                            System.out.println("图片信息: productId=" + img.getProductId() + 
                                             ", url=" + img.getUrl() + 
                                             ", isMain=" + img.getIsMain());
                        }
                        
                        boolean saveImagesResult = productImageService.saveBatch(productImages);
                        System.out.println("批量保存图片信息结果: " + saveImagesResult);
                        if (!saveImagesResult) {
                            throw new RuntimeException("保存商品图片信息失败");
                        }
                    } catch (Exception e) {
                        System.err.println("保存图片信息时发生错误: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("保存商品图片信息失败: " + e.getMessage());
                    }
                }
            } else {
                throw new RuntimeException("请至少上传一张商品图片");
            }
            
            System.out.println("=== 商品发布完成 ===");
        } catch (Exception e) {
            System.err.println("发布商品失败: " + e.getMessage());
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        try {
            // 1. 删除商品相关的图片
            productImageService.removeByProductId(id);
            
            // 2. 删除商品
            boolean success = removeById(id);
            if (!success) {
                throw new RuntimeException("删除商品失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除商品失败: " + e.getMessage());
        }
    }
    @Override
    public void updateProduct(Product product) {
        updateById(product);
    }
    @Override
    public Product addProduct(Product product) {
        save(product);
        return product;
    }
    @Override
    public void approveProduct(Long id) {
        baseMapper.approveProduct(id);
    }
    @Override
    public void rejectProduct(Long id) {
        baseMapper.rejectProduct(id);
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
   @Override
    public void auditProduct(Long id, boolean approved, String reason) {
        System.out.println("ProductServiceImpl.auditProduct called, id=" + id + ", approved=" + approved);
        int updated;
        if (approved) {
            updated = baseMapper.approveProduct(id);  // 状态改为1（在售）
        } else {
            updated = baseMapper.rejectProduct(id);   // 状态改为4（未通过）
            System.out.println("rejectProduct updated rows: " + updated);
        }
        System.out.println("审核SQL影响行数: " + updated);
    }
    @Override
    public Product getProductById(Long id) {
        return getById(id);
    }
    @Override
    public List<Product> getAllProducts() {
        return list();
    }
    @Override
    public List<Product> getPendingProducts() {
        // 你可以根据实际业务逻辑实现，这里假设待审核商品 status=0
        return baseMapper.selectPendingProducts();
    }
    @Autowired
    private ProductReviewDao productReviewDao;

    @Override
    public List<ProductReview> getProductReviewsByProductId(Long productId) {
        return productReviewDao.getReviewsByProductId(productId);
    }

    @Override
    public Long getTotalCount(Long categoryId, String keyword, Long merchantId, Integer status, Integer excludeStatus) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Product>()
            .eq(categoryId != null, Product::getCategoryId, categoryId)
            .like(StringUtils.isNotBlank(keyword), Product::getName, keyword)
            .eq(merchantId != null, Product::getMerchantId, merchantId)
            .eq(status != null, Product::getStatus, status)
            .ne(excludeStatus != null, Product::getStatus, excludeStatus)
            .eq(Product::getDeleted, 0));
    }

    @Override
    public List<Map<String, Object>> getCategoryStatsByMerchantId(Long merchantId) {
        return baseMapper.selectCategoryStatsByMerchantId(merchantId);
    }

    @Override
    public Double getTotalSalesByMerchantId(Long merchantId) {
        return baseMapper.getTotalSalesByMerchantId(merchantId);
    }

    @Override
    public Map<String, Object> getProducts(String keyword, Integer status, int page, int size, Long merchantId) {
        int offset = (page - 1) * size;
        List<Product> products = baseMapper.selectProductListWithImage(null, keyword, merchantId, status, null, size, offset);
        Long total = getTotalCount(null, keyword, merchantId, status, null);
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", total);
        return result;
    }
}

