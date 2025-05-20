package com.secondtrade.dao;

import com.secondtrade.entity.UserAddress;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserAddressDao {
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND deleted = 0")
    List<UserAddress> selectByUserId(Long userId);

    @Insert("INSERT INTO user_address (user_id, receiver_name, receiver_phone, province, city, district, detail, is_default) VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault})")
    int insert(UserAddress address);

    @Update("UPDATE user_address SET receiver_name=#{receiverName}, receiver_phone=#{receiverPhone}, province=#{province}, city=#{city}, district=#{district}, detail=#{detail} WHERE id=#{id} AND user_id=#{userId}")
    int update(UserAddress address);

    @Delete("DELETE FROM user_address WHERE id=#{addressId} AND user_id=#{userId}")
    int deleteByUserIdAndId(@Param("userId") Long userId, @Param("addressId") Long addressId);

    @Select("SELECT COUNT(*) FROM user_address WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(Long userId);

    @Update("UPDATE user_address SET is_default=0 WHERE user_id=#{userId}")
    int clearDefault(Long userId);

    @Update("UPDATE user_address SET is_default=1 WHERE user_id=#{userId} AND id=#{addressId}")
    int setDefault(@Param("userId") Long userId, @Param("addressId") Long addressId);

    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND is_default = 1 AND deleted = 0 LIMIT 1")
    UserAddress selectDefaultByUserId(Long userId);
}