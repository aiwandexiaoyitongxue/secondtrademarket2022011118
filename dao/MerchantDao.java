package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.Merchant;
import org.apache.ibatis.annotations.*;
import com.secondtrade.entity.User;
import com.secondtrade.entity.Merchant;
import com.secondtrade.entity.Product;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface MerchantDao extends BaseMapper<Merchant> {


    // 审核通过
    @Update("UPDATE merchant SET status = 1, level = 1 WHERE id = #{id}")
    int approveMerchant(Long id);

    // 审核驳回
    @Update("UPDATE merchant SET status = 2 WHERE id = #{id}")
    int rejectMerchant(Long id);

    // 禁用商家
    @Update("UPDATE merchant SET status = 2, deleted = 1 WHERE id = #{id}")
    int disableMerchant(Long id);

    // 查询所有商家
    @Select("SELECT * FROM merchant WHERE deleted=0")
    List<Merchant> selectAllMerchants();
    @Select("SELECT * FROM user WHERE status = 0 AND deleted = 0")
    List<User> selectPendingUsers();

    @Select("SELECT * FROM merchant WHERE status = 0 AND deleted = 0")
    List<Merchant> selectPendingMerchants();

    @Select("SELECT * FROM product WHERE status = 0 AND deleted = 0")
List<Product> selectPendingProducts();
    // 根据ID查找
    @Select("SELECT * FROM merchant WHERE id=#{id} AND deleted=0")
    Merchant selectById(Long id);
    @Update("UPDATE merchant SET level=#{level} WHERE id=#{id}")
    int updateMerchantLevel(@Param("id") Long id, @Param("level") Integer level);
    @Insert("INSERT INTO merchant (user_id, business_license, id_card, name, description, created_time, updated_time, deleted) " +
        "VALUES (#{userId}, #{businessLicense}, #{idCard}, #{name}, #{description}, NOW(), NOW(), 0)")
    void insertMerchant(Merchant merchant);
    /**
     * 根据用户ID查询商家信息
     * @param userId 用户ID
     * @return 商家信息
     */
    @Select("SELECT * FROM merchant WHERE user_id = #{userId} AND deleted = 0")
    Merchant selectByUserId(Long userId);

    @Select("SELECT * FROM merchant WHERE id = #{merchantId} AND deleted = 0")
    Merchant getMerchantInfoById(@Param("merchantId") Long merchantId);

    @Select("SELECT * FROM merchant WHERE id = #{id} AND deleted = 0")
    Merchant getMerchantById(@Param("id") Long id);

    @Update("UPDATE merchant SET total_sales = #{totalSales}, updated_time = NOW() WHERE id = #{id}")
    void updateMerchant(Merchant merchant);
}