package com.secondtrade.service;

import com.secondtrade.entity.WalletRecord;
import java.util.List;

public interface WalletRecordService {
    /**
     * 查询用户钱包余额
     */
    Double getBalanceByUserId(Long userId);

    /**
     * 查询用户钱包流水（充值/支付等）
     * @param userId 用户ID
     * @param type 类型：1-充值，2-支付，3-提现，4-退款，5-管理员加款
     */
    List<WalletRecord> getRecordsByType(Long userId, int type);

    /**
     * 查询用户所有钱包流水记录
     */
    List<WalletRecord> getAllRecords(Long userId);

    /**
     * 查询商家所有钱包流水记录
     */
    List<WalletRecord> getAllRecordsByMerchantId(Long merchantId);

    /**
     * 用户充值
     * @param userId 用户ID
     * @param amount 充值金额
     */
    void recharge(Long userId, Double amount);

    /**
     * 用户支付
     * @param userId 用户ID
     * @param amount 支付金额
     * @param orderNo 订单号
     */
    void payment(Long userId, Double amount, String orderNo);

    /**
     * 添加钱包流水记录
     */
    void addRecord(Long userId, Integer type, Double amount, Double balance, String remark);

    /**
     * 获取用户所有钱包流水记录
     */
    List<WalletRecord> getRecords(Long userId);
}