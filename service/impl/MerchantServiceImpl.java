package com.secondtrade.service.impl;

import com.secondtrade.dao.MerchantDao;
import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantDao merchantDao;

    @Override
    public List<Merchant> getPendingMerchants() {
        return merchantDao.selectPendingMerchants();
    }

    @Override
    public void approveMerchant(Long id) {
        merchantDao.approveMerchant(id);
    }

    @Override
    public void rejectMerchant(Long id) {
        merchantDao.rejectMerchant(id);
    }

    @Override
    public void disableMerchant(Long id) {
        merchantDao.disableMerchant(id);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return merchantDao.selectAllMerchants();
    }

    @Override
    public Merchant getMerchantById(Long id) {
        return merchantDao.selectById(id);
    }
      @Override
    public void updateMerchantLevel(Long id, Integer level) {
        merchantDao.updateMerchantLevel(id, level);
    }
}