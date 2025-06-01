package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import com.secondtrade.entity.Merchant;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.Order;
import com.secondtrade.entity.UserAddress;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
@Mapper
@Repository
public interface UserDao extends BaseMapper<User> {


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

// 查看用户详情
@Select("SELECT * FROM user WHERE id = #{id}")
User selectUserById(Long id);


/**
     * 获取待审核商品列表
     */
@Select("SELECT * FROM product WHERE status = 0 AND deleted = 0")
List<Product> selectPendingProducts();
/**
 * 获取待审核用户列表
 */
@Select("SELECT * FROM user WHERE status = 0 AND deleted = 0")
List<User> selectPendingUsers();
/**
     * 获取用户钱包信息
     */
    @Select("SELECT wallet_balance, points FROM user WHERE id = #{userId} AND deleted = 0")
    User getUserWallet(Long userId);

    /**
     * 根据状态获取用户订单列表
     */
    @Select("SELECT * FROM product_order WHERE user_id = #{userId} AND deleted = 0")
    List<Order> getUserOrders(Long userId);

    /**
     * 获取用户地址列表
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND deleted = 0")
    List<UserAddress> getUserAddresses(Long userId);
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);

    @Select("SELECT * FROM product_order WHERE user_id = #{userId} AND status = #{status} AND deleted = 0")
   List<Order> getUserOrdersByStatus(@Param("userId") Long userId, @Param("status") String status);
    @Select("SELECT wallet_balance FROM user WHERE id = #{userId}")
    Double getWalletBalanceByUserId(Long userId);
    @Select("SELECT points FROM user WHERE id = #{userId}")
     int getPointsByUserId(@Param("userId") Long userId);
     // 修改用户信息，包含所有字段
    @Update("UPDATE user SET " +
            "username=#{username}, " +
            "real_name=#{realName}, " +
            "phone=#{phone}, " +
            "email=#{email}, " +
            "city=#{city}, " +
            "gender=#{gender}, " +
            "role=#{role}, " +
            "status=#{status}, " +
            "description=#{description}, " +
            "wechat=#{wechat}, " +
            "avatar=#{avatar}, " +
            "updated_time=NOW() " +
            "WHERE id=#{id}")
    int updateUser(User user);

    @Update("UPDATE user SET points = #{points} WHERE id = #{userId}")
    void updatePoints(@Param("userId") Long userId, @Param("points") int points);
    
   @Update("UPDATE user SET wallet_balance = #{balance} WHERE id = #{userId}")
   int updateWalletBalance(@Param("userId") Long userId, @Param("balance") Double balance);

    /**
     * 根据用户ID获取商家ID
     */
    @Select("SELECT merchant_id FROM user WHERE id = #{userId}")
    Long getMerchantIdByUserId(@Param("userId") Long userId);
}