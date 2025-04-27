package com.secondtrade.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String city;
    private Integer gender;
    private String bankAccount;
    private Integer role;
    private Integer status;
    private BigDecimal walletBalance;
    private Integer points;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted;
}