CREATE TABLE IF NOT EXISTS merchant_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    images TEXT COMMENT '图片URL，多个用逗号分隔',
    is_anonymous BOOLEAN DEFAULT FALSE COMMENT '是否匿名',
    reply TEXT COMMENT '商家回复',
    reply_time DATETIME COMMENT '回复时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家评价表'; 