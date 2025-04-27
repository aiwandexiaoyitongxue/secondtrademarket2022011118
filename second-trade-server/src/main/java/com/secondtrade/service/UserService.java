package com.secondtrade.service;

import com.secondtrade.entity.User;

public interface UserService {
    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 用户登录
     */
    User login(String username, String password);
}