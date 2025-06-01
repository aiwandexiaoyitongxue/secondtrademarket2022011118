package com.secondtrade.service;

import com.secondtrade.entity.CouponTemplate;
import java.util.List;

public interface CouponTemplateService {
    List<CouponTemplate> getAvailableTemplates();
    CouponTemplate getTemplateById(Long id);
    void incrementReceivedCount(Long id);
}