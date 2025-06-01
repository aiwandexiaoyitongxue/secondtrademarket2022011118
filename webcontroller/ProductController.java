package com.secondtrade.webcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.secondtrade.common.Result;
import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import com.secondtrade.security.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import com.secondtrade.entity.ProductReview;
import com.secondtrade.service.ProductReviewService;
import java.util.HashMap;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.service.ProductImageService;
import java.util.stream.Collectors;

@Api(tags = "商品管理")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductReviewService productReviewService;
    @Autowired
    private ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long merchantId) {
        try {
            Map<String, Object> result = productService.getProducts(keyword, status, page, size, merchantId);
            Object list = result.get("list");
            Object total = result.get("total");
            Map<String, Object> data = Map.of(
                "records", list,
                "total", total
            );
            return ResponseEntity.ok(Map.of("code", 200, "data", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> publishProduct(
            @RequestParam("productJson") String productJson,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("merchantId") Long merchantId,
            Authentication authentication) {
        System.out.println("当前认证信息: " + authentication);
        if (authentication != null) {
            System.out.println("当前用户权限: " + authentication.getAuthorities());
        }
        try {
            // 打印认证信息
            System.out.println("认证信息: " + (authentication != null ? authentication.toString() : "null"));
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
                SecurityUser user = (SecurityUser) authentication.getPrincipal();
                System.out.println("当前用户ID: " + user.getId());
                System.out.println("当前用户角色: " + user.getAuthorities());
            }

            // 打印请求参数，用于调试
            System.out.println("接收到的商品数据: " + productJson);
            System.out.println("接收到的图片数量: " + (files != null ? files.size() : 0));
            System.out.println("接收到的商家ID: " + merchantId);

            // 验证商家ID
            if (merchantId == null) {
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商家ID不能为空"));
            }

            // 验证图片
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "请至少上传一张商品图片"));
            }

            // 验证图片大小和类型
            for (MultipartFile file : files) {
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "图片大小不能超过10MB"));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "只能上传图片文件"));
                }
            }

            // 验证商品数据
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> productData = objectMapper.readValue(productJson, Map.class);
                
                // 验证必填字段
                if (productData.get("name") == null || ((String) productData.get("name")).trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商品名称不能为空"));
                }
                if (productData.get("price") == null || Double.parseDouble(productData.get("price").toString()) <= 0) {
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商品价格必须大于0"));
                }
                if (productData.get("stock") == null || Integer.parseInt(productData.get("stock").toString()) <= 0) {
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商品库存必须大于0"));
                }
                if (productData.get("categoryId") == null) {
                    return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商品分类不能为空"));
                }
            } catch (Exception e) {
                System.err.println("解析商品数据失败: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "商品数据格式错误: " + e.getMessage()));
            }

            // 发布商品
            try {
            productService.publishProduct(productJson, files, merchantId);
            return ResponseEntity.ok(Map.of("code", 200, "message", "商品发布成功"));
        } catch (Exception e) {
            System.err.println("发布商品失败: " + e.getMessage());
            e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("code", 500, "message", "发布商品失败: " + e.getMessage()));
            }
        } catch (Exception e) {
            System.err.println("处理请求失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "服务器内部错误: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            productService.updateProduct(product);
            return ResponseEntity.ok(Map.of("success", true, "message", "商品更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            System.out.println("获取商品详情，ID: " + id);
            Product product = productService.getProductDetail(id);
            if (product == null) {
                System.out.println("商品不存在，ID: " + id);
                return ResponseEntity.status(404).body(Map.of("code", 404, "message", "商品不存在"));
            }
            
            // 获取商品图片列表
            List<ProductImage> images = productImageService.findByProductId(id);
            System.out.println("商品图片数量: " + images.size());
            List<String> imageUrls = images.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
            System.out.println("商品图片URLs: " + imageUrls);
                
            // 将图片列表添加到返回数据中
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", product);
            response.put("imageList", imageUrls);  // 修改为 imageList
            
            System.out.println("返回商品详情数据: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取商品详情失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    @ApiOperation("获取商品评价列表")
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        try {
            List<ProductReview> reviews = productReviewService.getReviewsByProductId(productId);
            return ResponseEntity.ok(Map.of("success", true, "data", reviews));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @ApiOperation("获取商品平均评分")
    @GetMapping("/{productId}/average-rating")
    public ResponseEntity<?> getProductAverageRating(@PathVariable Long productId) {
        try {
            Double averageRating = productReviewService.getAverageRatingByProductId(productId);
            return ResponseEntity.ok(Map.of("code", 200, "data", averageRating != null ? averageRating : 0.0));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }

    @ApiOperation("删除商品")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        try {
            // 获取当前用户信息
            SecurityUser user = (SecurityUser) authentication.getPrincipal();
            Long userId = user.getId();
            
            // 获取商品信息
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(404).body(Map.of("code", 404, "message", "商品不存在"));
            }
            
            // 验证是否是商品所属商家
            if (!product.getMerchantId().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权删除此商品"));
            }
            
            // 删除商品
            productService.deleteProduct(id);
            
            return ResponseEntity.ok(Map.of("code", 200, "message", "商品删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", "删除商品失败: " + e.getMessage()));
        }
    }
}