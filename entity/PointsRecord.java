package com.secondtrade.entity;

import java.util.Date;

public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer change; // 变动积分
    private Integer type;   // 1-获得，2-消费
    private Long orderId;
    private String remark;
    private Date createdTime;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getChange() { return change; }
    public void setChange(Integer change) { this.change = change; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
}