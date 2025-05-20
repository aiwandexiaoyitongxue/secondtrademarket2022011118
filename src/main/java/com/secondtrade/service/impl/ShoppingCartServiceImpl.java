package com.secondtrade.service.impl;

import com.secondtrade.dao.ShoppingCartDao;
import com.secondtrade.entity.ShoppingCart;
import com.secondtrade.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.secondtrade.dto.CartItemDTO;
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartDao shoppingCartDao;

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
    public void addCartItem(ShoppingCart cart) {
        shoppingCartDao.addCartItem(cart);
    }

    @Override
    public void clearCart(Long userId) {
        shoppingCartDao.clearCart(userId);
    }
    @Override
    public List<CartItemDTO> getCartItemDTOsByUserId(Long userId) {
        return shoppingCartDao.getCartItemDTOsByUserId(userId);
    }
}