-- 为product_id=1的商品添加示例图片
INSERT INTO `product_image` (`product_id`, `url`, `sort`, `is_main`, `created_time`, `updated_time`, `deleted`)
VALUES 
-- 主图
(1, '/static/images/products/sample1_main.jpg', 0, 1, NOW(), NOW(), 0),
-- 附图
(1, '/static/images/products/sample1_1.jpg', 1, 0, NOW(), NOW(), 0),
(1, '/static/images/products/sample1_2.jpg', 2, 0, NOW(), NOW(), 0);

-- 为product_id=2的商品添加示例图片(如果存在)
INSERT INTO `product_image` (`product_id`, `url`, `sort`, `is_main`, `created_time`, `updated_time`, `deleted`)
VALUES 
-- 主图
(2, '/static/images/products/sample2_main.jpg', 0, 1, NOW(), NOW(), 0),
-- 附图
(2, '/static/images/products/sample2_1.jpg', 1, 0, NOW(), NOW(), 0),
(2, '/static/images/products/sample2_2.jpg', 2, 0, NOW(), NOW(), 0);

-- 为product_id=3的商品添加示例图片(如果存在)
INSERT INTO `product_image` (`product_id`, `url`, `sort`, `is_main`, `created_time`, `updated_time`, `deleted`)
VALUES 
-- 主图
(3, '/static/images/products/sample3_main.jpg', 0, 1, NOW(), NOW(), 0),
-- 附图
(3, '/static/images/products/sample3_1.jpg', 1, 0, NOW(), NOW(), 0),
(3, '/static/images/products/sample3_2.jpg', 2, 0, NOW(), NOW(), 0); 