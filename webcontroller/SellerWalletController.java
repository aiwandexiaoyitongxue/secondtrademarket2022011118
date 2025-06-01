package com.secondtrade.webcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import com.secondtrade.service.WalletRecordService;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.entity.WalletRecord;
import com.secondtrade.dao.MerchantDao;
import com.secondtrade.entity.Merchant;

@RestController
@RequestMapping("/api/seller/wallet")
public class SellerWalletController {

    @Autowired
    private WalletRecordService walletService;

    @Autowired
    private MerchantDao merchantDao;

    // 账户余额
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Double balance = walletService.getBalanceByUserId(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of("balance", balance)
        ));
    }

    // 获取所有流水记录
    @GetMapping("/records/all")
    public ResponseEntity<?> getAllRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        // 根据用户ID获取商家信息
        Merchant merchant = merchantDao.selectByUserId(userId);
        if (merchant == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "未找到商家信息"
            ));
        }
        // 获取商家ID
        Long merchantId = merchant.getId();

        List<WalletRecord> records = walletService.getAllRecordsByMerchantId(merchantId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of("records", records)
        ));
    }
} 