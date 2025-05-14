package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.secondtrade.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import com.secondtrade.entity.Merchant;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.User;
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
    /**

/**
 * 审核通过
 */
@Update("UPDATE user SET status = 1 WHERE id = #{id}")
int approveUser(Long id);

/**
 * 审核驳回
 */
@Update("UPDATE user SET status = 2 WHERE id = #{id}")
int rejectUser(Long id);

/**
 * 查询所有用户
 */
@Select("SELECT * FROM user WHERE deleted = 0")
List<User> selectAllUsers();

/**
 * 禁用用户
 */
@Update("UPDATE user SET status = 2 WHERE id = #{id}")
int disableUser(Long id);
// 修改用户信息
@Update("UPDATE user SET username=#{username}, real_name=#{realName}, phone=#{phone}, email=#{email}, city=#{city}, gender=#{gender} WHERE id=#{id}")
int updateUser(User user);

// 查看用户详情
@Select("SELECT * FROM user WHERE id = #{id}")
User selectUserById(Long id);


@Select("SELECT * FROM merchant WHERE status = 0 AND deleted = 0")
List<Merchant> selectPendingMerchants();

@Select("SELECT * FROM product WHERE status = 0 AND deleted = 0")
List<Product> selectPendingProducts();
  // 获取待审核用户列表
    List<User> selectPendingUsers();
}