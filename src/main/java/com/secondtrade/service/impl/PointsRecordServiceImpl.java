package com.secondtrade.service.impl;

import com.secondtrade.dao.PointsRecordDao;
import com.secondtrade.entity.PointsRecord;
import com.secondtrade.service.PointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointsRecordServiceImpl implements PointsRecordService {

    @Autowired
    private PointsRecordDao pointsRecordDao;

    @Override
    public List<PointsRecord> getPointsRecords(Long userId) {
        return pointsRecordDao.selectByUserId(userId);
    }
}