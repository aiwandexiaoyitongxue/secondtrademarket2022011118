package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.dao.UserDao;
import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.secondtrade.entity.User;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    
    @Autowired
    private UserDao userDao;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void register(User user) {
        // 1. 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查手机号是否已存在
        if (userDao.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        // 3. 设置用户初始信息
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 密码加密
        user.setRealName(user.getUsername()); // 如果没有提供真实姓名，默认使用用户名
        //user.setRole(1); // 默认普通用户
        user.setStatus(1); // 正常状态
        user.setWalletBalance(new BigDecimal("0")); // 初始余额0
        user.setPoints(0); // 初始积分0
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0); // 未删除

        // 4. 保存用户信息
        save(user);
    }

    @Override
    public User login(String username, String password) {
        // 1. 查找用户
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户状态
        if (user.getStatus() == 2) {
            throw new RuntimeException("账号已被禁用");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }
    @Override
public List<User> getPendingUsers() {
    return userDao.selectPendingUsers();
}

@Override
public void approveUser(Long id) {
    userDao.approveUser(id);
}

@Override
public void rejectUser(Long id) {
    userDao.rejectUser(id);
}

@Override
public List<User> getAllUsers() {
    return userDao.selectAllUsers();
}

@Override
public void disableUser(Long id) {
    userDao.disableUser(id);
}

@Override
public void updateUser(User user) {
    userDao.updateUser(user);
}

@Override
public User getUserById(Long id) {
    return userDao.selectUserById(id);
}
}