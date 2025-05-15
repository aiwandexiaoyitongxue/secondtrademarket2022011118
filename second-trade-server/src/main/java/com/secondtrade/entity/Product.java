package com.secondtrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long merchantId;
    
    private Long categoryId;
    
    private String name;
    
    private String description;
    
    private BigDecimal originalPrice;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private Integer sales;
    
    private Integer condition;
    
    private String size;
    
    private Integer isNegotiable;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
} 