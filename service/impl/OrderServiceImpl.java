package com.secondtrade.service.impl;

import com.secondtrade.dao.OrderDao;
import com.secondtrade.entity.Order;
import com.secondtrade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.secondtrade.dao.OrderItemDao;
import com.secondtrade.entity.OrderItem;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.User;
import com.secondtrade.service.ProductService;
import com.secondtrade.service.PaymentService;
import com.secondtrade.service.UserService;
import com.secondtrade.service.ProductImageService;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.service.ShoppingCartService;
import com.secondtrade.service.CouponService;
import com.secondtrade.entity.Coupon;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MerchantService merchantService;

    @Override
    public Order getOrderByIdAndUserId(Long orderId, Long userId) {
        return orderDao.getOrderByIdAndUserId(orderId, userId);
    }

    @Override
    public void applyReturn(Long orderId, Long orderItemId, Long userId) throws Exception {
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) throw new Exception("订单不存在");
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        if (order.getStatus() == null || order.getStatus() != 3) throw new Exception("订单未收货，不能申请退货");
        long now = System.currentTimeMillis();
        long receivedTime = order.getReceiveTime().getTime();
        if (now - receivedTime > 24 * 60 * 60 * 1000) throw new Exception("收货已超过24小时，不能申请退货");
        if (item.getRefundStatus() != null && item.getRefundStatus() == 1) throw new Exception("该订单项已申请退货");
        orderItemDao.updateRefundStatus(orderItemId, 1); // 1=申请中
    }

    @Override
    public void auditReturn(Long orderId, Long orderItemId, Long merchantId, boolean approved) throws Exception {
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        if (!item.getMerchantId().equals(merchantId)) throw new Exception("无权操作此订单");
        if (item.getRefundStatus() == null || item.getRefundStatus() != 1) throw new Exception("该订单项未申请退货");
        if (approved) {
            orderItemDao.updateRefundStatus(orderItemId, 2); // 2=已同意
        } else {
            orderItemDao.updateRefundStatus(orderItemId, 3); // 3=已拒绝
        }
    }

    @Override
    public Integer getReturnStatus(Long orderId, Long orderItemId, Long userId) throws Exception {
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) throw new Exception("订单不存在");
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        return item.getRefundStatus();
    }

    @Override
    public void confirmReceipt(Long orderId, Long userId) throws Exception {
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) throw new Exception("订单不存在");
        if (order.getStatus() != 2) throw new Exception("订单状态不允许确认收货");

        // 更新订单状态为已完成
        orderDao.updateStatusAndReceiveTime(orderId, 3, new Date(), userId);

        // 订单完成后，将支付金额划给商家（扣除平台费用）
        if (order.getPayAmount() != null && order.getPayAmount() > 0 && order.getMerchantId() != null) {
            // 1. 获取商家信息和等级
            Merchant merchant = merchantService.getMerchantInfoById(order.getMerchantId());
            if (merchant == null) {
                throw new Exception("商家不存在");
            }
            Integer merchantLevel = merchant.getLevel();

            // 2. 根据商家等级获取费率
            double commissionRate = 0.0; // 默认费率
            switch (merchantLevel) {
                case 1:
                    commissionRate = 0.001; // 0.1%
                    break;
                case 2:
                    commissionRate = 0.002; // 0.2%
                    break;
                case 3:
                    commissionRate = 0.005; // 0.5%
                    break;
                case 4:
                    commissionRate = 0.0075; // 0.75%
                    break;
                case 5:
                    commissionRate = 0.01; // 1%
                    break;
                default:
                    // 如果商家等级不在预设范围内，可以抛出异常或使用默认费率
                    // 这里我们使用默认费率 1% (对应等级5)
                    commissionRate = 0.01;
                    break;
            }

            // 3. 计算平台费用和划给商家的金额
            double totalAmount = order.getPayAmount();
            double commission = totalAmount * commissionRate;
            double amountToMerchant = totalAmount - commission;

            // 4. 将划给商家的金额增加到商家钱包余额并记录流水
            userService.addMerchantBalance(merchant.getUserId(), amountToMerchant);

            // TODO: 记录平台收入流水 (可选)
        }
    }

    @Override
    public List<Order> getMerchantOrders(Long merchantId, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        return orderDao.getMerchantOrders(merchantId, status, offset, size);
    }

    @Override
    public int countMerchantOrders(Long merchantId, Integer status) {
        return orderDao.countMerchantOrders(merchantId, status);
    }

    @Override
    public Map<String, Object> getMerchantOrderStatistics(Long merchantId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取各状态订单数量
        stats.put("total", orderDao.countMerchantOrders(merchantId, null));
        stats.put("pendingPayment", orderDao.countMerchantOrders(merchantId, 1));
        stats.put("pendingDelivery", orderDao.countMerchantOrders(merchantId, 2));
        stats.put("pendingReceipt", orderDao.countMerchantOrders(merchantId, 3));
        stats.put("completed", orderDao.countMerchantOrders(merchantId, 4));
        stats.put("cancelled", orderDao.countMerchantOrders(merchantId, 5));
        
        // 获取今日订单数和金额
        stats.put("todayOrders", orderDao.countTodayMerchantOrders(merchantId));
        stats.put("todayAmount", orderDao.sumTodayMerchantOrders(merchantId));
        
        // 获取本月订单数和金额
        stats.put("monthOrders", orderDao.countMonthMerchantOrders(merchantId));
        stats.put("monthAmount", orderDao.sumMonthMerchantOrders(merchantId));
        
        return stats;
    }

    @Override
    public List<Order> getMerchantRefundApplyOrders(Long merchantId) {
        return orderDao.getMerchantRefundApplyOrders(merchantId);
    }

    @Override
    public List<Order> getMerchantRefundedOrders(Long merchantId) {
        return orderDao.getMerchantRefundedOrders(merchantId);
    }

    @Override
    public void processRefund(Long orderId, Long merchantId, boolean approved, String reason) throws Exception {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (!order.getMerchantId().equals(merchantId)) {
            throw new Exception("无权操作此订单");
        }
        
        if (approved) {
            // 同意退款
            orderDao.updateOrderStatus(orderId, 6); // 6-已退款
            orderDao.updateRefundTime(orderId, new Date());
        } else {
            // 拒绝退款
            orderDao.updateOrderStatus(orderId, 7); // 7-退款被拒绝
            orderDao.updateRefundReason(orderId, reason);
        }
    }
    @Override
    public Order getOrderByIdAndMerchantId(Long orderId, Long merchantId) {
        return orderDao.getOrderByIdAndMerchantId(orderId, merchantId);
    }
     @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkout(Long userId, Long productId, Integer quantity, String tradeType,
                        Long addressId, String payMethod, Boolean usePoints, Long couponId) throws Exception {
        // 1. 验证商品
        Product product = productService.getProductById(productId);
        if (product == null || product.getStatus() != ProductService.PRODUCT_STATUS_ON_SALE) {
            throw new Exception("商品不存在或已下架");
        }
        
        // 2. 验证库存
        if (product.getStock() < quantity) {
            throw new Exception("商品库存不足");
        }
        
        // 3. 验证交易方式
        if ("express".equals(tradeType) && addressId == null) {
            throw new Exception("请选择收货地址");
        }
        
        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(product.getMerchantId());
        order.setStatus(0); // 待付款
        order.setCreatedTime(new Date());
        order.setUpdatedTime(new Date());
        order.setDeleted(0);
        order.setTradeType(tradeType); // 设置交易类型
        order.setAddressId(addressId); // 设置地址ID

       // 5. 计算订单金额
        Double totalAmount = product.getPrice() * quantity;
        order.setTotalAmount(totalAmount);
        
        // 6. 处理积分抵扣
        if (usePoints != null && usePoints) {
            User user = userService.getUserById(userId);
            Double pointsDeduction = paymentService.calculatePointsDeduction(userId, totalAmount);
            order.setPointsAmount(pointsDeduction);
            order.setPointsUsed((int)(pointsDeduction * 100));
            totalAmount = totalAmount - pointsDeduction;
        }
        
        // 7. 处理优惠券
        Double couponDeduction = 0.0;
        if (couponId != null) {
            // 获取并验证优惠券
            Coupon coupon = couponService.getCouponByIdAndUserId(couponId, userId);
            if (coupon == null || coupon.getStatus() != 0) { // 0表示未使用
                throw new Exception("优惠券无效或已使用");
            }
            if (coupon.getEndTime().before(new Date())) {
                 throw new Exception("优惠券已过期");
            }
            if (coupon.getMinAmount().doubleValue() > totalAmount) {
                throw new Exception("订单金额未满足优惠券使用条件");
            }

            // 计算优惠券抵扣金额
            couponDeduction = coupon.getAmount().doubleValue();

            // 确保抵扣金额不超过订单总金额
            if (couponDeduction > totalAmount) {
                couponDeduction = totalAmount;
            }

            // 记录使用的优惠券ID和金额
            order.setCouponId(couponId);
            order.setCouponAmount(couponDeduction);
        }

        // 从总金额中减去优惠券抵扣
        totalAmount -= couponDeduction;

        order.setPayAmount(totalAmount);
        // 8. 保存订单
        orderDao.insert(order);
        
        // 9. 创建订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(productId);
        orderItem.setMerchantId(product.getMerchantId());
        orderItem.setProductName(product.getName());
        
        // 从product_image表获取商品主图
        List<ProductImage> images = productImageService.findByProductId(productId);
        String productImage = "/static/images/default-product.jpg"; // 默认图片
        if (!images.isEmpty()) {
            // 查找主图
            for (ProductImage image : images) {
                if (image.getIsMain() == 1) {
                    productImage = image.getUrl();
                    break;
                }
            }
            // 如果没有主图，使用第一张图片
            if (productImage.equals("/static/images/default-product.jpg") && !images.isEmpty()) {
                productImage = images.get(0).getUrl();
            }
        }
        orderItem.setProductImage(productImage);
        
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());
        orderItem.setTotalAmount(product.getPrice() * quantity);
        orderItem.setCreatedTime(new Date());
        orderItem.setUpdatedTime(new Date());
        orderItem.setDeleted(0);
        
        orderItemDao.insert(orderItem);
        
        // 10. 扣减库存
        product.setStock(product.getStock() - quantity);
        productService.updateProduct(product);
        
        // 11. 处理支付
        if ("balance".equals(payMethod)) {
            // 余额支付
            paymentService.processPayment(order.getOrderNo(), "balance");
        } else if ("points".equals(payMethod)) {
             paymentService.processPayment(order.getOrderNo(), "points");
        } else {
             throw new Exception("不支持的支付方式");
        }

        // 支付成功后，删除购物车中的对应商品
        List<OrderItem> orderItems = getOrderItemsByOrderId(order.getId());
        List<Long> productIds = new ArrayList<>();
        for (OrderItem item : orderItems) {
            productIds.add(item.getProductId());
        }
        if (!productIds.isEmpty()) {
            shoppingCartService.deleteCartItems(userId, productIds);
        }

        // 支付成功后，更新使用的优惠券状态为已使用
        if (couponId != null) {
            couponService.useCoupon(userId, couponId, order.getId(), BigDecimal.valueOf(totalAmount));
        }
    }
    
    private String generateOrderNo() {
        // 生成订单号：时间戳 + 6位随机数
        return System.currentTimeMillis() + String.format("%06d", (int)(Math.random() * 1000000));
    }
    // 在 OrderServiceImpl.java 中添加
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderDao.getOrderByOrderNo(orderNo);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderDao.getOrderById(orderId);
    }
    
    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        orderDao.updateOrderStatus(orderId, status);
    }
    
    // 确保所有金额计算都使用 Double
    private Double calculateTotalAmount(Order order) {
        return order.getTotalAmount() - (order.getPointsAmount() != null ? order.getPointsAmount() : 0.0);
    }
    @Override
    public List<Order> getUserOrders(Long userId, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        return orderDao.getUserOrders(userId, status, offset, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCheckout(Long userId, List<Map<String, Object>> items, String tradeType,
                             Long addressId, String payMethod, Boolean usePoints, Long couponId) throws Exception {
        // 验证交易方式
        if ("express".equals(tradeType) && addressId == null) {
            throw new Exception("请选择收货地址");
        }

        // 验证商品
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new Exception("商品不存在");
            }
            if (product.getStock() < Integer.valueOf(item.get("quantity").toString())) {
                throw new Exception("商品库存不足");
            }
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(0); // 待支付
        order.setTradeType(tradeType);
        order.setAddressId(addressId);
        order.setCreatedTime(new Date());
        order.setUpdatedTime(new Date());
        order.setDeleted(0);
        order.setPointsAmount(0.0); // 设置默认值
        order.setPointsUsed(0); // 设置默认值

        // 设置商家ID（使用第一个商品的商家ID）
        if (!items.isEmpty()) {
            Long firstProductId = Long.valueOf(items.get(0).get("productId").toString());
            Product firstProduct = productService.getProductById(firstProductId);
            if (firstProduct != null) {
                order.setMerchantId(firstProduct.getMerchantId());
            }
        }

        // 计算订单总金额
        double totalAmount = 0;
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Product product = productService.getProductById(productId);
            int quantity = Integer.valueOf(item.get("quantity").toString());
            totalAmount += product.getPrice() * quantity;
        }
        order.setTotalAmount(totalAmount);

        // 处理积分抵扣
        if (usePoints != null && usePoints) {
            Double pointsDeduction = paymentService.calculatePointsDeduction(userId, totalAmount);
            order.setPointsAmount(pointsDeduction);
            order.setPointsUsed((int)(pointsDeduction * 100));
            totalAmount = totalAmount - pointsDeduction;
        }

        // 处理优惠券
        Double couponDeduction = 0.0;
        if (couponId != null) {
            // 获取并验证优惠券
            Coupon coupon = couponService.getCouponByIdAndUserId(couponId, userId);
            if (coupon == null || coupon.getStatus() != 0) { // 0表示未使用
                throw new Exception("优惠券无效或已使用");
            }
            if (coupon.getEndTime().before(new Date())) {
                 throw new Exception("优惠券已过期");
            }
            if (coupon.getMinAmount().doubleValue() > totalAmount) {
                throw new Exception("订单金额未满足优惠券使用条件");
            }

            // 计算优惠券抵扣金额
            couponDeduction = coupon.getAmount().doubleValue();

            // 确保抵扣金额不超过订单总金额
            if (couponDeduction > totalAmount) {
                couponDeduction = totalAmount;
            }

            // 记录使用的优惠券ID和金额
            order.setCouponId(couponId);
            order.setCouponAmount(couponDeduction);
        }

        // 从总金额中减去优惠券抵扣
        totalAmount -= couponDeduction;

        order.setPayAmount(totalAmount);

        // 保存订单
        orderDao.insert(order);

        // 创建订单项
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new Exception("商品不存在");
            }
            int quantity = Integer.valueOf(item.get("quantity").toString());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(productId);
            orderItem.setMerchantId(product.getMerchantId());
            orderItem.setProductName(product.getName());
            
            // 从product_image表获取商品主图
            List<ProductImage> images = productImageService.findByProductId(productId);
            String productImage = "/static/images/default-product.jpg"; // 默认图片
            if (!images.isEmpty()) {
                // 查找主图
                for (ProductImage image : images) {
                    if (image.getIsMain() == 1) {
                        productImage = image.getUrl();
                        break;
                    }
                }
                // 如果没有主图，使用第一张图片
                if (productImage.equals("/static/images/default-product.jpg") && !images.isEmpty()) {
                    productImage = images.get(0).getUrl();
                }
            }
            orderItem.setProductImage(productImage);
            
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            orderItem.setTotalAmount(product.getPrice() * quantity);
            orderItem.setCreatedTime(new Date());
            orderItem.setUpdatedTime(new Date());
            orderItem.setDeleted(0);
            orderItemDao.insert(orderItem);

            // 更新商品库存
            product.setStock(product.getStock() - quantity);
            product.setSales(product.getSales() + quantity);
            productService.updateProduct(product);
        }

        // 处理支付
        try {
            if ("balance".equals(payMethod)) {
                // 余额支付
                paymentService.processPayment(order.getOrderNo(), "balance");
            } else if ("points".equals(payMethod)) {
                // 积分支付
                paymentService.processPayment(order.getOrderNo(), "points");
            }

            // 支付成功后，更新使用的优惠券状态为已使用
            if (couponId != null) {
                couponService.useCoupon(userId, couponId, order.getId(), BigDecimal.valueOf(totalAmount));
            }

        } catch (Exception e) {
            // 如果支付失败，考虑回滚订单创建、库存扣减、销量增加等操作
            // 由于使用了 @Transactional，异常抛出时会自动回滚
            throw new Exception("支付处理失败: " + e.getMessage());
        }
    }

    // 实现获取订单项的方法
    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        if (orderId == null) {
            return new ArrayList<>(); // 或者抛出异常，取决于需求
        }
        return orderItemDao.getItemsByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 3) { // 3表示待收货状态
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单状态为已完成
        order.setStatus(4); // 4表示已完成
        order.setReceiveTime(new Date());
        orderDao.updateOrderStatus(orderId, 4);
        
        // 更新商家总销售额
        Merchant merchant = merchantService.getMerchantById(order.getMerchantId());
        if (merchant != null) {
            BigDecimal currentTotalSales = merchant.getTotalSales();
            BigDecimal orderAmount = BigDecimal.valueOf(order.getPayAmount());
            merchant.setTotalSales(currentTotalSales.add(orderAmount));
            merchantService.updateMerchant(merchant);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long userId) throws Exception {
        // 获取订单
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        
        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new Exception("订单状态不正确，无法支付");
        }
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        
        // 检查余额
        if (user.getWalletBalance() < order.getPayAmount()) {
            throw new Exception("余额不足");
        }
        
        // 扣除用户余额
        Double newBalance = user.getWalletBalance() - order.getPayAmount();
        userService.updateWalletBalance(userId, newBalance);
        
        // 更新订单状态为待发货
        order.setStatus(1);
        order.setPayTime(new Date());
        orderDao.updateOrderStatus(orderId, 1);
    }
}