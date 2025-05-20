package com.secondtrade.webcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.secondtrade.service.WalletRecordService;  // 改为 WalletRecordService
import com.secondtrade.security.SecurityUser;
import com.secondtrade.entity.WalletRecord;

@RestController
@RequestMapping("/api/user/wallet")
public class WalletController {

    @Autowired
    private WalletRecordService walletService;  // 改为 WalletRecordService

    // 账户余额
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        BigDecimal balance = walletService.getBalanceByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "balance", balance));
    }

    // 充值记录
    @GetMapping("/recharge")
    public ResponseEntity<?> getRechargeRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<WalletRecord> records = walletService.getRecordsByType(userId, 1); // 1-充值
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }

    // 支付记录
    @GetMapping("/payment")
    public ResponseEntity<?> getPaymentRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<WalletRecord> records = walletService.getRecordsByType(userId, 2); // 2-支付
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }
}