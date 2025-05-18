package com.secondtrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 自定义插入方法
     */
    int customInsert(User user);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
} 