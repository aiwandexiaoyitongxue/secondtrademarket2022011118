package com.secondtrade.webcontroller;

import com.secondtrade.entity.PointsRecord;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.PointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/points")
public class PointsController {

    @Autowired
    private PointsRecordService pointsRecordService;

    @GetMapping("/records")
    public ResponseEntity<?> getPointsRecords(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<PointsRecord> records = pointsRecordService.getPointsRecords(userId);
        return ResponseEntity.ok(Map.of("success", true, "records", records));
    }
}