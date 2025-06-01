package com.secondtrade.service;

import com.secondtrade.entity.Product;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import com.secondtrade.entity.ProductReview;
import com.secondtrade.dao.ProductReviewDao;
public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    List<Product> getPendingProducts();
    void approveProduct(Long id);
    void rejectProduct(Long id);
    Product addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Long id);
    Map<String, Object> getAllProducts(Long merchantId, Integer status, Integer excludeStatus, int page, int size);
    List<Product> getProductList(Long categoryId, String keyword, Long merchantId, Integer status, Integer excludeStatus, Integer page, Integer size);
    Product getProductDetail(Long id);
    void publishProduct(String productJson, List<MultipartFile> images, Long merchantId) throws Exception;
    void auditProduct(Long id, boolean approved, String reason) throws Exception;
    // 商品状态
    public static final int PRODUCT_STATUS_PENDING = 0;    // 待审核
    public static final int PRODUCT_STATUS_ON_SALE = 1;    // 在售
    public static final int PRODUCT_STATUS_OFF_SHELF = 2;  // 已下架
    public static final int PRODUCT_STATUS_SOLD_OUT = 3;   // 已售罄
    public static final int PRODUCT_STATUS_REJECTED = 4;   // 未通过
    List<ProductReview> getProductReviewsByProductId(Long productId);
    
    // 获取商品总数
    Long getTotalCount(Long categoryId, String keyword, Long merchantId, Integer status, Integer excludeStatus);

    /**
     * 获取商家商品分类统计
     * @param merchantId 商家ID
     * @return 分类统计列表，包含分类名称和商品数量
     */
    List<Map<String, Object>> getCategoryStatsByMerchantId(Long merchantId);

    Double getTotalSalesByMerchantId(Long merchantId);

    /**
     * 获取商品列表
     * @param keyword 搜索关键词
     * @param status 商品状态
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表和总数
     */
    Map<String, Object> getProducts(String keyword, Integer status, int page, int size, Long merchantId);
}