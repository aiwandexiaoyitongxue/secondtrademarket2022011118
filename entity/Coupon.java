package com.secondtrade.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Coupon {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal minAmount;
    private Date startTime;
    private Date endTime;
    private Integer status; // 0-未使用 1-已使用 2-已过期
    private Date createdTime;
    private Long orderId; // 关联的订单ID
    private Long templateId; // 关联的优惠券模板ID

    // Getters and Setters
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    // 判断优惠券是否可用
    public boolean isAvailable() {
        Date now = new Date();
        return status == 0 && now.after(startTime) && now.before(endTime);
    }

    // 判断订单金额是否满足优惠券使用条件
    public boolean isSatisfyMinAmount(BigDecimal orderAmount) {
        return orderAmount.compareTo(minAmount) >= 0;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", minAmount=" + minAmount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", createdTime=" + createdTime +
                ", orderId=" + orderId +
                ", templateId=" + templateId +
                '}';
    }
}