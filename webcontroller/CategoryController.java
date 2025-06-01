package com.secondtrade.webcontroller;

import com.secondtrade.common.Result;
import com.secondtrade.entity.Category;
import com.secondtrade.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(tags = "分类管理")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("获取所有分类")
    @GetMapping("/all")
    public Result<List<Category>> getAllCategories() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }
} 