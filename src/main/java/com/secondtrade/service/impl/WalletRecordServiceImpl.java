package com.secondtrade.service.impl;

import com.secondtrade.dao.UserDao;
import com.secondtrade.dao.WalletRecordDao;
import com.secondtrade.entity.WalletRecord;
import com.secondtrade.service.WalletRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletRecordServiceImpl implements WalletRecordService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WalletRecordDao walletRecordDao;

    @Override
    public BigDecimal getBalanceByUserId(Long userId) {
        BigDecimal balance = userDao.getWalletBalanceByUserId(userId);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    @Override
    public List<WalletRecord> getRecordsByType(Long userId, int type) {
        return walletRecordDao.selectByUserIdAndType(userId, type);
    }
}