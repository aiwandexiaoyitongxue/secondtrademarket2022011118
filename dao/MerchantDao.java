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
    @Update("UPDATE merchant SET level=2 WHERE id=#{id}")
    int approveMerchant(Long id);

    // 审核驳回
    @Update("UPDATE merchant SET level=0 WHERE id=#{id}")
    int rejectMerchant(Long id);

    // 禁用商家
    @Update("UPDATE merchant SET deleted=1 WHERE id=#{id}")
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
}