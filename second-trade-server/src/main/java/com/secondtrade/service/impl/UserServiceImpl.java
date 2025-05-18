package com.secondtrade.service.impl;

import com.secondtrade.entity.User;
import com.secondtrade.entity.Merchant;
import com.secondtrade.mapper.UserMapper;
import com.secondtrade.mapper.MerchantMapper;
import com.secondtrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(User user) {
        // 检查用户名是否存在
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
 
        // 检查手机号是否存在
        if (userMapper.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        // 检查邮箱是否存在
        if (userMapper.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 设置默认值
        user.setStatus(1); // 默认状态为正常
        user.setWalletBalance(new BigDecimal("0.00")); // 默认钱包余额为0
        user.setPoints(0); // 默认积分为0
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0); // 默认未删除

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 使用自定义插入方法
        userMapper.customInsert(user);

        // 如果用户是商家，创建商家信息
        if (user.getRole() == 1) {
            // 重新查询用户以获取ID
            User savedUser = userMapper.findByUsername(user.getUsername());
            if (savedUser == null || savedUser.getId() == null) {
                throw new RuntimeException("用户创建失败");
            }

            Merchant merchant = new Merchant();
            merchant.setUserId(savedUser.getId());
            merchant.setBusinessLicense(""); // 设置默认值
            merchant.setIdCard(""); // 设置默认值
            merchant.setLevel(1); // 默认等级为1
            merchant.setSatisfactionRate(new BigDecimal("5.00")); // 默认满意度为5.00
            merchant.setTotalSales(new BigDecimal("0.00")); // 默认销售额为0
            merchant.setCreatedTime(LocalDateTime.now());
            merchant.setUpdatedTime(LocalDateTime.now());
            merchant.setDeleted(0);
            
            merchantMapper.insert(merchant);
        }
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号状态异常");
        }

        return user;
    }
}