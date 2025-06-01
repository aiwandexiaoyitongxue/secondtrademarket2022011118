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
import java.util.Date;

@RestController
@RequestMapping("/api/user/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 获取购物车
   @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    System.out.println("getCart userId = " + userId);
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
    public ResponseEntity<?> addCartItem(Authentication authentication, @RequestBody Map<String, Object> body) {
        try {
            System.out.println("Received request body: " + body);
            
            // 获取用户ID
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            System.out.println("User ID: " + userId);
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "用户未登录"));
            }

            // 参数校验
            if (body == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请求数据不能为空"));
            }

            // 获取并验证必要参数
            Long productId = null;
            Integer quantity = null;
            Long merchantId = null;

            try {
                System.out.println("productId from request: " + body.get("productId"));
                System.out.println("quantity from request: " + body.get("quantity"));
                System.out.println("merchantId from request: " + body.get("merchantId"));
                
                productId = Long.valueOf(body.get("productId").toString());
                quantity = Integer.valueOf(body.get("quantity").toString());
                merchantId = Long.valueOf(body.get("merchantId").toString());
                
                System.out.println("Parsed values - productId: " + productId + ", quantity: " + quantity + ", merchantId: " + merchantId);
            } catch (Exception e) {
                System.err.println("Error parsing request parameters: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "参数格式错误: " + e.getMessage()));
            }

            // 参数有效性检查
            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "商品ID无效"));
            }
            if (quantity == null || quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "商品数量必须大于0"));
            }
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "商家ID无效"));
            }

            // 创建购物车对象
            ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setMerchantId(merchantId);
            cart.setQuantity(quantity);
            cart.setSelected(body.get("selected") != null ? Integer.valueOf(body.get("selected").toString()) : 1);
            cart.setCreatedTime(new Date());
            cart.setUpdatedTime(new Date());
            cart.setDeleted(0);

            System.out.println("Created cart object: " + cart);

            // 添加到购物车
        shoppingCartService.addCartItem(cart);
            return ResponseEntity.ok(Map.of("success", true, "message", "添加成功"));
        } catch (NumberFormatException e) {
            System.err.println("参数格式错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "参数格式错误: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("添加购物车失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "添加购物车失败: " + e.getMessage()));
        }
    }

    // 清空购物车
    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        shoppingCartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}