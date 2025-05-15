package com.secondtrade.entity;

import lombok.Data;

@Data
public class MerchantLevelRate {
    private Integer level;
    private Double rate;
    private String description;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}