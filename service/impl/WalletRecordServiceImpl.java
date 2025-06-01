package com.secondtrade.service.impl;

import com.secondtrade.dao.UserDao;
import com.secondtrade.dao.WalletRecordDao;
import com.secondtrade.entity.WalletRecord;
import com.secondtrade.service.WalletRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class WalletRecordServiceImpl implements WalletRecordService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WalletRecordDao walletRecordDao;

    @Override
    public Double getBalanceByUserId(Long userId) {
        return userDao.getWalletBalanceByUserId(userId);
    }

    @Override
    public List<WalletRecord> getRecordsByType(Long userId, int type) {
        return walletRecordDao.selectByUserIdAndType(userId, type);
    }

    @Override
    public List<WalletRecord> getAllRecords(Long userId) {
        return walletRecordDao.selectAllByUserId(userId);
    }

    @Override
    public List<WalletRecord> getAllRecordsByMerchantId(Long merchantId) {
        return walletRecordDao.selectAllByMerchantId(merchantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(Long userId, Double amount) {
        // 1. 查询用户当前余额
        Double currentBalance = getBalanceByUserId(userId);
        
        // 2. 计算新余额
        Double newBalance = currentBalance + amount;
        
        // 3. 更新用户余额
        userDao.updateWalletBalance(userId, newBalance);
        
        // 4. 记录充值流水
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setType(1); // 1-充值
        record.setAmount(amount);
        record.setBalance(newBalance);
        record.setRemark("账户充值");
        record.setCreatedTime(new Date());
        walletRecordDao.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payment(Long userId, Double amount, String orderNo) {
        // 1. 查询用户当前余额
        Double currentBalance = getBalanceByUserId(userId);
        
        // 2. 检查余额是否足够
        if (currentBalance < amount) {
            throw new RuntimeException("余额不足");
        }
        
        // 3. 计算新余额
        Double newBalance = currentBalance - amount;
        
        // 4. 更新用户余额
        userDao.updateWalletBalance(userId, newBalance);
        
        // 5. 记录支付流水
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setType(2); // 2-支付
        record.setAmount(amount);
        record.setBalance(newBalance);
        record.setRemark("订单支付 - " + orderNo);
        record.setCreatedTime(new Date());
        walletRecordDao.insert(record);
    }

    @Override
    public void addRecord(Long userId, Integer type, Double amount, Double balance, String remark) {
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalance(balance);
        record.setRemark(remark);
        record.setCreatedTime(new Date());
        walletRecordDao.insert(record);
    }

    @Override
    public List<WalletRecord> getRecords(Long userId) {
        return walletRecordDao.selectAllByUserId(userId);
    }
}