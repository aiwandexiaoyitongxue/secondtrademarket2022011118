package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.entity.Category;
import com.secondtrade.dao.CategoryMapper;
import com.secondtrade.service.CategoryService;
import org.springframework.stereotype.Service;
            
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
} 