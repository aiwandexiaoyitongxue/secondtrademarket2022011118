package com.secondtrade.common.constants;

import java.math.BigDecimal;

/**
 * 系统常量
 */
public class Constants {
    // 用户角色
    public static final int ROLE_USER = 1;        // 普通用户
    public static final int ROLE_MERCHANT = 2;    // 商家
    public static final int ROLE_ADMIN = 3;       // 管理员

    // 用户状态
    public static final int USER_STATUS_PENDING = 0;   // 待审核
    public static final int USER_STATUS_NORMAL = 1;    // 正常
    public static final int USER_STATUS_DISABLED = 2;  // 禁用

    // 商品状态
    public static final int PRODUCT_STATUS_PENDING = 0;    // 待审核
    public static final int PRODUCT_STATUS_ON_SALE = 1;    // 在售
    public static final int PRODUCT_STATUS_OFF_SHELF = 2;  // 已下架
    public static final int PRODUCT_STATUS_SOLD_OUT = 3;   // 已售罄

    // 订单状态
    public static final int ORDER_STATUS_PENDING_PAY = 0;      // 待付款
    public static final int ORDER_STATUS_PENDING_SHIP = 1;     // 待发货
    public static final int ORDER_STATUS_PENDING_RECEIVE = 2;  // 待收货
    public static final int ORDER_STATUS_COMPLETED = 3;        // 已完成
    public static final int ORDER_STATUS_CANCELLED = 4;        // 已取消
    public static final int ORDER_STATUS_REFUNDED = 5;        // 已退款

    // 删除标记
    public static final int NOT_DELETED = 0;  // 未删除
    public static final int DELETED = 1;      // 已删除

    // 验证码类型
    public static final int VERIFY_CODE_TYPE_REGISTER = 1;    // 注册
    public static final int VERIFY_CODE_TYPE_LOGIN = 2;       // 登录
    public static final int VERIFY_CODE_TYPE_RESET_PWD = 3;   // 重置密码
    public static final int VERIFY_CODE_TYPE_BIND_PHONE = 4;  // 绑定手机
    public static final int VERIFY_CODE_TYPE_BIND_EMAIL = 5;  // 绑定邮箱

    // 积分相关
    public static final BigDecimal POINTS_RATE = new BigDecimal("0.01");           // 积分兑换比例
    public static final BigDecimal POINTS_DISCOUNT_RATE = new BigDecimal("100");   // 积分抵扣比例

    // 商家等级费率
    public static final BigDecimal MERCHANT_LEVEL_1_RATE = new BigDecimal("0.10");  // 一级商家费率
    public static final BigDecimal MERCHANT_LEVEL_2_RATE = new BigDecimal("0.20");  // 二级商家费率
    public static final BigDecimal MERCHANT_LEVEL_3_RATE = new BigDecimal("0.50");  // 三级商家费率
    public static final BigDecimal MERCHANT_LEVEL_4_RATE = new BigDecimal("0.75");  // 四级商家费率
    public static final BigDecimal MERCHANT_LEVEL_5_RATE = new BigDecimal("1.00");  // 五级商家费率
}