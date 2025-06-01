package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.dao.MerchantDao;
import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.secondtrade.dao.MerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantDao, Merchant> implements MerchantService {
    @Autowired
    private MerchantDao merchantDao;

    @Override
    public List<Merchant> getPendingMerchants() {
        return baseMapper.selectPendingMerchants();
    }

    @Override
    public void approveMerchant(Long id) {
        baseMapper.approveMerchant(id);
    }

    @Override
    public void rejectMerchant(Long id) {
        baseMapper.rejectMerchant(id);
    }

    @Override
    public void disableMerchant(Long id) {
        baseMapper.disableMerchant(id);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return baseMapper.selectAllMerchants();
    }

    @Override
    public Merchant getMerchantById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void updateMerchantLevel(Long id, Integer level) {
        baseMapper.updateMerchantLevel(id, level);
    }

    @Override
    public void insertMerchant(Merchant merchant) {
        baseMapper.insertMerchant(merchant);
    }

    @Override
    public Merchant getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Merchant getMerchantByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return merchantDao.selectByUserId(userId);
    }

    @Override
    public Merchant getMerchantInfoById(Long merchantId) {
        return merchantDao.getMerchantById(merchantId);
    }

    @Override
    public void updateMerchant(Merchant merchant) {
        merchantDao.updateMerchant(merchant);
    }

    @Override
    public void createMerchant(Merchant merchant) {
        merchant.setCreatedTime(LocalDateTime.now());
        merchant.setUpdatedTime(LocalDateTime.now());
        merchant.setDeleted(0);
        merchant.setStatus(0); // 设置为待审核状态
        merchantDao.insertMerchant(merchant);
    }
}