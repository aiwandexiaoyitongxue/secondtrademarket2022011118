package com.secondtrade.entity;

import java.util.Date;

public class CouponTemplate {
    private Long id;
    private String name;
    private Double amount;
    private Double minAmount;
    private Date startTime;
    private Date endTime;
    private Integer totalCount;
    private Integer receivedCount;
    private String description;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(Integer receivedCount) {
        this.receivedCount = receivedCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 判断优惠券模板是否可领取
    public boolean isAvailable() {
        Date now = new Date();
        boolean timeValid = now.after(startTime) && now.before(endTime);
        boolean countValid = totalCount == 0 || receivedCount < totalCount;
        return timeValid && countValid;
    }

    @Override
    public String toString() {
        return "CouponTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", minAmount=" + minAmount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalCount=" + totalCount +
                ", receivedCount=" + receivedCount +
                ", description='" + description + '\'' +
                '}';
    }
}
