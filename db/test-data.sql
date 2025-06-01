-- 插入商家评价测试数据
INSERT INTO merchant_review (
    user_id,
    merchant_id,
    order_id,
    rating,
    content,
    is_anonymous,
    created_time,
    updated_time,
    deleted
) VALUES 
(2, 1, 1, 5, '商家服务态度很好，商品质量也不错！', 0, NOW(), NOW(), 0),
(2, 1, 1, 4, '商品包装很好，物流速度快，就是价格稍贵。', 0, NOW(), NOW(), 0),
(2, 1, 1, 5, '非常满意的一次购物体验，下次还会来！', 0, NOW(), NOW(), 0),
(2, 1, 1, 3, '商品一般，但是客服态度很好。', 0, NOW(), NOW(), 0),
(2, 1, 1, 5, '商品和描述的一样，很满意！', 0, NOW(), NOW(), 0);

-- 更新一些评价的回复
UPDATE merchant_review 
SET reply = '感谢您的支持，我们会继续努力提供更好的服务！',
    reply_time = NOW()
WHERE id = 1;

UPDATE merchant_review 
SET reply = '感谢您的反馈，我们会继续优化价格策略。',
    reply_time = NOW()
WHERE id = 2; 