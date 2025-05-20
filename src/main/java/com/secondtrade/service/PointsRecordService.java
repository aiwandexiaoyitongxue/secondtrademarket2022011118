package com.secondtrade.service;

import com.secondtrade.entity.PointsRecord;
import java.util.List;

public interface PointsRecordService {
    List<PointsRecord> getPointsRecords(Long userId);
}