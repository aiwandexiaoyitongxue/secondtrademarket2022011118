package com.secondtrade.service.impl;

import com.secondtrade.dao.CouponTemplateDao;
import com.secondtrade.entity.CouponTemplate;
import com.secondtrade.service.CouponTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {
    @Autowired
    private CouponTemplateDao couponTemplateDao;

    @Override
    public List<CouponTemplate> getAvailableTemplates() {
        return couponTemplateDao.selectAvailableTemplates(new Date());
    }

    @Override
    public CouponTemplate getTemplateById(Long id) {
        return couponTemplateDao.selectById(id);
    }

    @Override
    public void incrementReceivedCount(Long id) {
        couponTemplateDao.incrementReceivedCount(id);
    }
}