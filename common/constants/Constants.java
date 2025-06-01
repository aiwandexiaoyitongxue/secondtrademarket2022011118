 package com.secondtrade.common.constants;

public class Constants {
    // 用户角色
    public static final int ROLE_USER = 1;  // 普通用户
    public static final int ROLE_ADMIN = 2; // 管理员

    // 用户状态
    public static final int STATUS_NORMAL = 1;   // 正常
    public static final int STATUS_DISABLED = 2;  // 禁用

    // 商品状态
    public static final int PRODUCT_STATUS_PENDING = 0;    // 待审核
    public static final int PRODUCT_STATUS_APPROVED = 1;   // 已通过
    public static final int PRODUCT_STATUS_REJECTED = 2;   // 已拒绝

    // 删除标记
    public static final int NOT_DELETED = 0;  // 未删除
    public static final int IS_DELETED = 1;   // 已删除

    // 分页默认值
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 1;

    // Token相关
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24小时
}