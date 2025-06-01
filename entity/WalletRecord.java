package com.secondtrade.entity;

import java.util.Date;

public class WalletRecord {
    private Long id;
    private Long userId;
    private Integer type; // 1-充值，2-支付，3-提现，4-退款，5-管理员加款
    private Double amount;
    private Double balance;
    private String remark;
    private Date createdTime;

    /**
     * 商家收入类型
     */
    public static final int TYPE_MERCHANT_INCOME = 5;

    // getters and setters

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}