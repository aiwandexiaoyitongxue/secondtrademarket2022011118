package com.secondtrade.webcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.secondtrade.common.Result;
import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "商品管理")
@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @ApiOperation("发布商品")
    @PostMapping("/publish")
    public Result<Void> publishProduct(@RequestParam("product") String productJson,
                                     @RequestParam("images") List<MultipartFile> images) {
        try {
            productService.publishProduct(productJson, images);
            return Result.success(null);
        } catch (JsonProcessingException e) {
            return Result.error("商品数据格式错误: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("发布商品失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取商品列表")
    @GetMapping("/list")
    public Result<List<Product>> getProductList(@RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Long merchantId,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) Integer excludeStatus,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.getProductList(categoryId, keyword, merchantId, status, excludeStatus, page, size));
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @ApiOperation("更新商品")
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        productService.updateProduct(id, product);
        return Result.success(null);
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            System.out.println("接收到删除商品请求，商品ID: " + id);
        productService.deleteProduct(id);
            System.out.println("商品删除成功，ID: " + id);
        return Result.success(null);
        } catch (Exception e) {
            System.err.println("删除商品请求处理异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("删除商品失败: " + e.getMessage());
        }
    }
} 