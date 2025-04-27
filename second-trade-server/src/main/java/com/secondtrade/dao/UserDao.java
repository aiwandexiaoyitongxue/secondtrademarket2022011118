package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE username = #{username} AND deleted = 0")
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE phone = #{phone} AND deleted = 0")
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE email = #{email} AND deleted = 0")
    boolean existsByEmail(String email);
}