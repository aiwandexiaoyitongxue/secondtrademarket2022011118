-- 创建数据库
CREATE DATABASE `2022011118` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `2022011118`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `city` VARCHAR(50) COMMENT '城市',
    `gender` TINYINT COMMENT '性别：0-未知，1-男，2-女',
    `bank_account` VARCHAR(20) COMMENT '银行账号',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-商家，2-管理员',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-正常，2-禁用',
    `wallet_balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '钱包余额',
    `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
CREATE TABLE IF NOT EXISTS `wallet_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型：1-充值，2-支付，3-提现，4-退款，5-管理员加款',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `balance` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    `remark` VARCHAR(255) COMMENT '备注',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水记录表';
-- 商家信息表
CREATE TABLE IF NOT EXISTS `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商家ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `business_license` VARCHAR(255) NOT NULL COMMENT '营业执照图片URL',
    `id_card` VARCHAR(255) NOT NULL COMMENT '身份证图片URL',
    `level` TINYINT NOT NULL DEFAULT 1 COMMENT '商家等级：1-5',
    `satisfaction_rate` DECIMAL(3,2) NOT NULL DEFAULT 5.00 COMMENT '满意度评分',
    `total_sales` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总销售额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-正常，2-禁用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `name` VARCHAR(100) COMMENT '商家名称',
    `description` TEXT COMMENT '商家描述',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家信息表';

-- 商家等级费率表
CREATE TABLE IF NOT EXISTS `merchant_level_rate` (
    `level` TINYINT NOT NULL COMMENT '商家等级：1-5',
    `rate` DECIMAL(4,2) NOT NULL COMMENT '费率',
    `description` VARCHAR(255) COMMENT '描述',
    PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家等级费率表';

-- 插入商家等级费率数据
INSERT INTO `merchant_level_rate` (`level`, `rate`, `description`) VALUES
(1, 0.10, '一级商家'),
(2, 0.20, '二级商家'),
(3, 0.50, '三级商家'),
(4, 0.75, '四级商家'),
(5, 1.00, '五级商家');

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
    `level` TINYINT NOT NULL DEFAULT 1 COMMENT '层级：1-一级分类，2-二级分类',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
    `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `condition` TINYINT NOT NULL COMMENT '商品成色：1-全新，2-九成新，3-八成新，4-七成新，5-六成新及以下',
    `size` VARCHAR(50) COMMENT '尺寸',
    `is_negotiable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否可议价：0-不可议价，1-可议价',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-在售，2-已下架，3-已售罄',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS `product_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `is_main` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主图：0-否，1-是',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 插入一级分类
INSERT INTO `category` (`name`, `level`, `sort`) VALUES
('电子产品', 1, 1),
('服装服饰', 1, 2),
('图书教材', 1, 3),
('生活用品', 1, 4),
('运动器材', 1, 5);

-- 插入二级分类（以电子产品为例）
INSERT INTO `category` (`name`, `parent_id`, `level`, `sort`) VALUES
('手机', 1, 2, 1),
('电脑', 1, 2, 2),
('平板', 1, 2, 3),
('耳机', 1, 2, 4);

-- 订单表
CREATE TABLE IF NOT EXISTS `product_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `points_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额',
    `points_used` INT NOT NULL DEFAULT 0 COMMENT '使用积分',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已退款',
    `pay_time` DATETIME COMMENT '支付时间',
    `delivery_time` DATETIME COMMENT '发货时间',
    `receive_time` DATETIME COMMENT '收货时间',
    `cancel_time` DATETIME COMMENT '取消时间',
    `cancel_reason` VARCHAR(255) COMMENT '取消原因',
    `refund_time` DATETIME COMMENT '退款时间',
    `refund_reason` VARCHAR(255) COMMENT '退款原因',
    `auto_confirm_days` INT NOT NULL DEFAULT 7 COMMENT '自动确认收货天数',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255) NOT NULL COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';
ALTER TABLE order_item
ADD COLUMN refund_status TINYINT DEFAULT 0 COMMENT '退货状态：0-未申请，1-申请中，2-已同意，3-已拒绝，4-已完成',
ADD COLUMN refund_apply_time DATETIME COMMENT '退货申请时间',
ADD COLUMN refund_reason VARCHAR(255) COMMENT '退货原因',
ADD COLUMN refund_audit_time DATETIME COMMENT '商家审核时间',
ADD COLUMN refund_audit_remark VARCHAR(255) COMMENT '商家审核备注';
ALTER TABLE order_item ADD COLUMN merchant_id BIGINT NOT NULL COMMENT '商家ID';
-- 订单日志表
CREATE TABLE IF NOT EXISTS `order_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `status` TINYINT NOT NULL COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已退款',
    `note` VARCHAR(255) COMMENT '备注',
    `operator` VARCHAR(50) COMMENT '操作人',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_item_id` BIGINT NOT NULL COMMENT '订单项ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
    `content` TEXT COMMENT '评价内容',
    `images` VARCHAR(1000) COMMENT '评价图片，多个图片用逗号分隔',
    `is_anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    `reply` TEXT COMMENT '商家回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';
ALTER TABLE product_review ADD COLUMN service_rating TINYINT NOT NULL DEFAULT 5 COMMENT '服务态度评分：1-5星';
-- 商家评价表
CREATE TABLE IF NOT EXISTS `merchant_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
    `content` TEXT COMMENT '评价内容',
    `images` VARCHAR(1000) COMMENT '评价图片，多个图片用逗号分隔',
    `is_anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    `reply` TEXT COMMENT '商家回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家评价表';
-- 购物车表
CREATE TABLE IF NOT EXISTS `shopping_cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中：0-未选中，1-已选中',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
-- 验证码表
CREATE TABLE IF NOT EXISTS `verification_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
    `type` TINYINT NOT NULL COMMENT '验证码类型：1-注册，2-登录，3-修改密码，4-绑定手机，5-绑定邮箱',
    `target` VARCHAR(100) NOT NULL COMMENT '接收目标（手机号/邮箱）',
    `code` VARCHAR(6) NOT NULL COMMENT '验证码',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_target` (`target`),
    KEY `idx_type` (`type`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';
CREATE TABLE IF NOT EXISTS `points_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `change` INT NOT NULL COMMENT '变动积分，正为获得，负为消耗',
    `type` TINYINT NOT NULL COMMENT '类型：1-获得，2-消费',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单',
    `remark` VARCHAR(255),
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分明细表';
-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `config_type` TINYINT NOT NULL DEFAULT 1 COMMENT '配置类型：1-系统配置，2-业务配置',
    `description` VARCHAR(255) COMMENT '配置描述',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
CREATE TABLE IF NOT EXISTS `user_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    `province` VARCHAR(50) COMMENT '省',
    `city` VARCHAR(50) COMMENT '市',
    `district` VARCHAR(50) COMMENT '区/县',
    `detail` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';
CREATE TABLE IF NOT EXISTS `coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '抵扣金额',
    `min_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '最低消费金额',
    `start_time` DATETIME NOT NULL COMMENT '生效时间',
    `end_time` DATETIME NOT NULL COMMENT '失效时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0-未使用 1-已使用 2-已过期',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 插入一些基础系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('SITE_NAME', '二手交易平台', 1, '网站名称'),
('SITE_LOGO', '/static/images/logo.png', 1, '网站Logo'),
('SITE_DESCRIPTION', '校园二手交易平台', 1, '网站描述'),
('UPLOAD_PATH', '/upload', 1, '文件上传路径'),
('MAX_UPLOAD_SIZE', '5242880', 1, '最大上传大小（字节）'),
('ALLOWED_FILE_TYPES', 'jpg,jpeg,png,gif', 1, '允许上传的文件类型'),
('POINTS_RATE', '0.01', 2, '积分兑换比例'),
('AUTO_CONFIRM_DAYS', '7', 2, '自动确认收货天数'),
('MIN_WITHDRAW_AMOUNT', '100', 2, '最小提现金额'),
('MAX_WITHDRAW_AMOUNT', '50000', 2, '最大提现金额');

-- 插入测试数据
INSERT INTO user (username, password, role, avatar, created_time, updated_time, deleted)
VALUES 
('merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 'https://example.com/avatar1.jpg', NOW(), NOW(), 0),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 0, 'https://example.com/avatar2.jpg', NOW(), NOW(), 0);

INSERT INTO merchant (user_id, name, description, created_time, updated_time, deleted)
VALUES (1, '测试商家', '这是一个测试商家', NOW(), NOW(), 0);

INSERT INTO product_order (order_no, user_id, merchant_id, total_amount, status, created_time, updated_time, deleted)
VALUES ('TEST202401010001', 2, 1, 100.00, 4, NOW(), NOW(), 0);

INSERT INTO merchant_review (user_id, merchant_id, order_id, rating, content, is_anonymous, created_time, updated_time, deleted)
VALUES (2, 1, 1, 5, '商家服务态度很好，商品质量也不错！', 0, NOW(), NOW(), 0);

SHOW TABLES; 