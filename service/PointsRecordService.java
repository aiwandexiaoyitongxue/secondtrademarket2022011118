package com.secondtrade.service;

import com.secondtrade.entity.PointsRecord;
import java.util.List;

public interface PointsRecordService {
    List<PointsRecord> getPointsRecords(Long userId);

    // 新增方法：添加积分记录
    void addPointsRecord(PointsRecord pointsRecord);
}