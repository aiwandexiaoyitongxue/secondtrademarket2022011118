package com.secondtrade.webcontroller;

import com.secondtrade.entity.ShoppingCart;
import com.secondtrade.service.ShoppingCartService;
import com.secondtrade.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.secondtrade.dto.CartItemDTO;

@RestController
@RequestMapping("/api/user/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 获取购物车
   @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    List<CartItemDTO> cart = shoppingCartService.getCartItemDTOsByUserId(userId);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("data", cart);
    return ResponseEntity.ok(response);
}

    // 更新数量
    @PostMapping("/update")
    public ResponseEntity<?> updateQuantity(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Long id = Long.valueOf(body.get("id").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        shoppingCartService.updateQuantity(id, userId, quantity);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 删除商品
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCartItem(Authentication authentication, @RequestBody Map<String, Object> body) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Long id = Long.valueOf(body.get("id").toString());
        shoppingCartService.deleteCartItem(id, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 添加商品
    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(Authentication authentication, @RequestBody ShoppingCart cart) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        cart.setUserId(userId);
        shoppingCartService.addCartItem(cart);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 清空购物车
    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        shoppingCartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}