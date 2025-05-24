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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import com.secondtrade.entity.ProductReview;
import com.secondtrade.dao.ProductReviewDao;
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${upload.path}")
    private String baseUploadDir;

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
        System.out.println("调用 selectProductListWithImage 参数: categoryId=" + categoryId + ", keyword=" + keyword + ", merchantId=" + merchantId + ", status=" + status + ", excludeStatus=" + excludeStatus + ", size=" + size + ", offset=" + offset);
        List<Product> products = baseMapper.selectProductListWithImage(categoryId, keyword, merchantId, status, excludeStatus, size, offset);
        for (Product p : products) {
            System.out.println("商品ID: " + p.getId() + ", image: " + p.getImage());
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
        System.out.println("接收到的商品数据: " + productJson);

        // 1. 解析商品数据
        Product product = objectMapper.readValue(productJson, Product.class);
        System.out.println("解析后的商品对象: " + product);
        product.setId(null); 
        // 2. 校验商家和分类
        if (merchantId == null) throw new RuntimeException("商家ID不能为空");
        if (!validateMerchant(merchantId)) throw new RuntimeException("商家不存在，ID: " + merchantId);
        if (product.getCategoryId() == null) throw new RuntimeException("商品类目不能为空");
        if (!validateCategory(product.getCategoryId())) throw new RuntimeException("商品分类不存在，ID: " + product.getCategoryId());

        // 3. 校验商品必填字段
        if (product.getName() == null || product.getName().trim().isEmpty()) throw new RuntimeException("商品名称不能为空");
       // 修改 publishProduct 方法中的价格判断
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("商品价格必须大于0");
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
        boolean saveResult = save(product); // MyBatis-Plus的save方法
        System.out.println("商品信息保存结果: " + saveResult);
        if (!saveResult) throw new RuntimeException("保存商品信息失败");

        // 6. 处理商品图片
        if (images != null && !images.isEmpty()) {
            System.out.println("开始处理商品图片，图片数量: " + images.size());
            List<ProductImage> productImages = new ArrayList<>();
            // 定义商品图片的子目录 (不再需要，或者设为空)
            // String imageSubDir = "images";

            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);

                if (file.isEmpty()) {
                    System.out.println("跳过空文件");
                    continue;
                }
                System.out.println("处理第" + (i+1) + "张图片: " + file.getOriginalFilename() + ", 大小: " + file.getSize() + "字节");
                // **修改这里：调用 FileUtil 中带有 baseUploadDir 参数，subDir 设为空字符串**
                String url = FileUtil.uploadFile(file, baseUploadDir, ""); // 将文件直接上传到 baseUploadDir 目录
                System.out.println("文件上传成功，返回的 URL 片段: " + url); // 打印返回的 URL 片段

                ProductImage image = new ProductImage();
                image.setProductId(product.getId());
                image.setUrl(url); // 将 FileUtil 返回的 URL 片段保存到 ProductImage 对象
                image.setIsMain(i == 0 ? 1 : 0); // 第一张为主图
                image.setCreatedTime(now);
                image.setUpdatedTime(now);
                image.setDeleted(0);
                productImages.add(image);
            }
            if (productImages.isEmpty()) throw new RuntimeException("没有成功上传的图片");
            boolean batchSaveResult = productImageService.saveBatch(productImages);
            if (!batchSaveResult) throw new RuntimeException("保存商品图片失败");
            System.out.println("商品图片保存完成");
        } else {
            System.out.println("没有图片需要处理");
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
    public void deleteProduct(Long id) {
        // 你可以根据实际业务做软删除或物理删除
        // 这里以物理删除为例
        removeById(id);
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
}

