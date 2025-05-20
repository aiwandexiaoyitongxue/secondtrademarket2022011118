package com.secondtrade.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    // 用户角色常量
    public static final int ROLE_USER = 0;      // 普通用户
    public static final int ROLE_MERCHANT = 1;  // 商家
    public static final int ROLE_ADMIN = 2;     // 管理员

    // 用户状态常量
    public static final int STATUS_PENDING = 0;  // 待审核
    public static final int STATUS_ACTIVE = 1;   // 正常
    public static final int STATUS_BANNED = 2;   // 封禁

    // 性别常量
    public static final int GENDER_UNKNOWN = 0;  // 未知
    public static final int GENDER_MALE = 1;     // 男
    public static final int GENDER_FEMALE = 2;   // 女

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    @TableField("real_name")
    private String realName;

    private String phone;

    private String email;

    private String city;

    private Integer gender;

    @TableField("bank_account")
    private String bankAccount;

    private Integer role;

    private Integer status;

    @TableField("wallet_balance")
    private BigDecimal walletBalance = BigDecimal.ZERO;

    private Integer points = 0;

    @TableField("avatar")
    private String avatar;

   @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted = 0;

    // 保留原有的getter/setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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

    // 业务方法
    public boolean isMerchant() {
        return ROLE_MERCHANT == this.role;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN == this.role;
    }

    public boolean isActive() {
        return STATUS_ACTIVE == this.status;
    }

    public boolean isPending() {
        return STATUS_PENDING == this.status;
    }

    public boolean isBanned() {
        return STATUS_BANNED == this.status;
    }

    // 添加积分
    public void addPoints(int points) {
        this.points += points;
    }

    // 使用积分
    public boolean usePoints(int points) {
        if (this.points >= points) {
            this.points -= points;
            return true;
        }
        return false;
    }

    // 充值钱包
    public void rechargeWallet(BigDecimal amount) {
        this.walletBalance = this.walletBalance.add(amount);
    }

    // 扣减钱包余额
    public boolean deductWallet(BigDecimal amount) {
        if (this.walletBalance.compareTo(amount) >= 0) {
            this.walletBalance = this.walletBalance.subtract(amount);
            return true;
        }
        return false;
    }

    // 获取可抵扣金额
    public BigDecimal getDeductibleAmount() {
        return new BigDecimal(this.points).divide(new BigDecimal("100"));
    }

}