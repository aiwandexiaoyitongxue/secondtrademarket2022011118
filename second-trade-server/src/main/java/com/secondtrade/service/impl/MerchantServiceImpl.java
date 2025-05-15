package com.secondtrade.service.impl;

import com.secondtrade.entity.Merchant;
import com.secondtrade.mapper.MerchantMapper;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Merchant merchant) {
        merchant.setCreatedTime(LocalDateTime.now());
        merchant.setUpdatedTime(LocalDateTime.now());
        merchant.setDeleted(0);
        merchantMapper.insert(merchant);
    }

    @Override
    public Merchant getByUserId(Long userId) {
        return merchantMapper.findByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Merchant merchant) {
        merchant.setUpdatedTime(LocalDateTime.now());
        merchantMapper.update(merchant);
    }

    @Override
    public Merchant getById(Long id) {
        return merchantMapper.findById(id);
    }
} 