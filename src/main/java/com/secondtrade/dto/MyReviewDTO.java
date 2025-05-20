package com.secondtrade.dto;

import java.util.Date;

public class MyReviewDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer rating;
    private String content;
    private String images;
    private Date createdTime;
    private Integer serviceRating;
    public Integer getServiceRating() { return serviceRating; }
    public void setServiceRating(Integer serviceRating) { this.serviceRating = serviceRating; }
    // getter/setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}