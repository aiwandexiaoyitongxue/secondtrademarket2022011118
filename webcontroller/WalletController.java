package com.secondtrade.webcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.secondtrade.service.WalletRecordService;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.entity.WalletRecord;

@RestController
@RequestMapping("/api/user/wallet")
public class WalletController {

    @Autowired
    private WalletRecordService walletService;

    // 账户余额
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Double balance = walletService.getBalanceByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "balance", balance));
    }

    // 充值
    @PostMapping("/recharge/do")
    public ResponseEntity<?> recharge(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            Double amount = Double.valueOf(body.get("amount").toString());
            walletService.recharge(userId, amount);
            return ResponseEntity.ok(Map.of("success", true, "message", "充值成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 充值记录
    @GetMapping("/recharge/records")
    public ResponseEntity<?> getRechargeRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<WalletRecord> records = walletService.getRecordsByType(userId, 1); // 1-充值
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }

    // 支付
    @PostMapping("/payment/do")
    public ResponseEntity<?> payment(
            Authentication authentication,
            @RequestParam Double amount,
            @RequestParam String orderNo) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            walletService.payment(userId, amount, orderNo);
            return ResponseEntity.ok(Map.of("success", true, "message", "支付成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 支付记录
    @GetMapping("/payment/records")
    public ResponseEntity<?> getPaymentRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<WalletRecord> records = walletService.getRecordsByType(userId, 2); // 2-支付
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }

    // 获取所有流水记录
    @GetMapping("/records/all")
    public ResponseEntity<?> getAllRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<WalletRecord> records = walletService.getAllRecords(userId);
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }
}