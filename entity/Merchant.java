package com.secondtrade.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Merchant {
    private Long id;
    private Long userId;
    private String businessLicense; // 营业执照图片URL
    private String idCard;          // 身份证图片URL
    private Integer level;          // 商家等级
    private BigDecimal satisfactionRate; // 满意度评分
    private BigDecimal totalSales;       // 总销售额
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted;
    private Integer status;  // 状态：0-待审核，1-正常，2-禁用

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public BigDecimal getSatisfactionRate() {
        return satisfactionRate;
    }

    public void setSatisfactionRate(BigDecimal satisfactionRate) {
        this.satisfactionRate = satisfactionRate;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}