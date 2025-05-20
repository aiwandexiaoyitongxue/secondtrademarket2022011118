package com.secondtrade.service;

import com.secondtrade.entity.ShoppingCart;
import java.util.List;
import com.secondtrade.dto.CartItemDTO;
public interface ShoppingCartService {
    List<ShoppingCart> getCartByUserId(Long userId);
    void updateQuantity(Long id, Long userId, Integer quantity);
    void deleteCartItem(Long id, Long userId);
    void addCartItem(ShoppingCart cart);
    void clearCart(Long userId);
    List<CartItemDTO> getCartItemDTOsByUserId(Long userId);
}