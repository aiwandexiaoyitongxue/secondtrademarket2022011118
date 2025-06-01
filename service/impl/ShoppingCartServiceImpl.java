package com.secondtrade.service.impl;

import com.secondtrade.dao.ShoppingCartDao;
import com.secondtrade.entity.ShoppingCart;
import com.secondtrade.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.secondtrade.dto.CartItemDTO;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import com.secondtrade.exception.BusinessException;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private ProductService productService;

    @Override
    public List<ShoppingCart> getCartByUserId(Long userId) {
        return shoppingCartDao.getCartByUserId(userId);
    }

    @Override
    public void updateQuantity(Long id, Long userId, Integer quantity) {
        shoppingCartDao.updateQuantity(id, userId, quantity);
    }

    @Override
    public void deleteCartItem(Long id, Long userId) {
        shoppingCartDao.deleteCartItem(id, userId);
    }

    @Override
    @Transactional
    public void addCartItem(ShoppingCart cart) {
        // 1. 检查商品是否存在
        Product product = productService.getProductById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 检查商品是否已下架
        if (product.getStatus() != ProductService.PRODUCT_STATUS_ON_SALE) {
            throw new BusinessException("商品已下架");
        }

        // 3. 检查商品库存
        if (product.getStock() < cart.getQuantity()) {
            throw new BusinessException("商品库存不足");
        }

        // 4. 检查购物车中是否已存在该商品
        ShoppingCart existingCart = shoppingCartDao.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
        
        if (existingCart != null) {
            // 如果商品已存在且未删除，则更新数量
            if (existingCart.getDeleted() == 0) {
                int newQuantity = existingCart.getQuantity() + cart.getQuantity();
                // 再次检查库存
                if (product.getStock() < newQuantity) {
                    throw new BusinessException("商品库存不足");
                }
                int updated = shoppingCartDao.updateQuantityByUserIdAndProductId(cart.getUserId(), cart.getProductId(), newQuantity);
                if (updated == 0) {
                    throw new BusinessException("更新购物车数量失败");
                }
            } else {
                // 如果商品已删除，则恢复该记录
                existingCart.setQuantity(cart.getQuantity());
                existingCart.setSelected(1);
                existingCart.setDeleted(0);
                existingCart.setUpdatedTime(new Date());
                int updated = shoppingCartDao.updateCartItem(existingCart);
                if (updated == 0) {
                    throw new BusinessException("恢复购物车商品失败");
                }
            }
        } else {
            // 5. 如果购物车中不存在该商品，则新增
            cart.setMerchantId(product.getMerchantId());
            cart.setSelected(1);
            cart.setDeleted(0);
            cart.setCreatedTime(new Date());
            cart.setUpdatedTime(new Date());
            
            try {
                int inserted = shoppingCartDao.addCartItem(cart);
                if (inserted == 0) {
                    throw new BusinessException("添加购物车失败");
                }
            } catch (Exception e) {
                // 如果插入失败，可能是并发问题，再次检查是否存在
                existingCart = shoppingCartDao.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
                if (existingCart != null && existingCart.getDeleted() == 0) {
                    // 如果确实存在，则更新数量
                    int newQuantity = existingCart.getQuantity() + cart.getQuantity();
                    if (product.getStock() < newQuantity) {
                        throw new BusinessException("商品库存不足");
                    }
                    int updated = shoppingCartDao.updateQuantityByUserIdAndProductId(cart.getUserId(), cart.getProductId(), newQuantity);
                    if (updated == 0) {
                        throw new BusinessException("更新购物车数量失败");
                    }
                } else {
                    throw new BusinessException("添加购物车失败: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void clearCart(Long userId) {
        shoppingCartDao.clearCart(userId);
    }

    @Override
    public List<CartItemDTO> getCartItemDTOsByUserId(Long userId) {
        return shoppingCartDao.getCartItemDTOsByUserId(userId);
    }

    @Override
    public void deleteCartItems(Long userId, List<Long> productIds) {
        if (userId == null || productIds == null || productIds.isEmpty()) {
            return;
        }
        shoppingCartDao.deleteByUserIdAndProductIds(userId, productIds);
    }
}