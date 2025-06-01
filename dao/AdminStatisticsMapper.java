package com.secondtrade.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminStatisticsMapper {

    // 用户总数（不含已删除）
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0")
    int countUsers();

    // 商家总数（不含已删除）
    @Select("SELECT COUNT(*) FROM merchant WHERE deleted = 0")
    int countMerchants();

    // 商品总数（不含已删除）
    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0")
    int countProducts();

    // 今日交易额（订单表，已支付且未删除，按pay_time当天统计）
    @Select("SELECT IFNULL(SUM(pay_amount),0) FROM product_order WHERE deleted = 0 AND pay_time IS NOT NULL AND DATE(pay_time) = CURDATE()")
    double sumTodayAmount();
    @Select({
    "<script>",
    "SELECT IFNULL(SUM(pay_amount),0) FROM product_order",
    "WHERE deleted = 0 AND pay_time IS NOT NULL",
    "AND pay_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)",
    "GROUP BY DATE(pay_time)",
    "ORDER BY DATE(pay_time)",
    "</script>"
})
List<Double> sumWeekAmount();
@Select("SELECT COUNT(*) FROM user WHERE status = 0 AND deleted = 0")
int countPendingUsers();
}