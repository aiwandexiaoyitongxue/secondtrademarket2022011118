package com.secondtrade.service;

import com.secondtrade.entity.WalletRecord;
import java.math.BigDecimal;
import java.util.List;

public interface WalletRecordService {  // 改为 WalletRecordService
    /**
     * 查询用户钱包余额
     */
    BigDecimal getBalanceByUserId(Long userId);

    /**
     * 查询用户钱包流水（充值/支付等）
     * @param userId 用户ID
     * @param type 类型：1-充值，2-支付，3-提现，4-退款，5-管理员加款
     */
    List<WalletRecord> getRecordsByType(Long userId, int type);
}