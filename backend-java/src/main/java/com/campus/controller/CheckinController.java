package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.CheckinRecord;
import com.campus.service.CheckinService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 晚寝签到控制器
 * 
 * @author Campus Platform Team
 */
@RestController
@RequestMapping("/checkin")
public class CheckinController {
    
    private final CheckinService checkinService;
    
    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }
    
    /**
     * 提交晚寝签到
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitCheckin(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String location = request.get("location");
        String remark = request.getOrDefault("remark", "");
        
        Map<String, Object> result = checkinService.submitCheckin(userId, location, remark);
        return Result.success(result);
    }
    
    /**
     * 获取签到记录列表
     */
    @GetMapping("/records")
    public Result<Page<CheckinRecord>> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Page<CheckinRecord> records = checkinService.getCheckinRecords(userId, page, pageSize);
        return Result.success(records);
    }
    
    /**
     * 获取今日签到状态
     */
    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayStatus(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> status = checkinService.getTodayStatus(userId);
        return Result.success(status);
    }
    
    /**
     * 获取签到统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> statistics = checkinService.getStatistics(userId);
        return Result.success(statistics);
    }
    
    /**
     * 审核签到记录（管理员功能）
     */
    @PostMapping("/approve/{recordId}")
    public Result<Map<String, Object>> approveCheckin(
            @PathVariable Long recordId,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        String reviewRemark = request.getOrDefault("reviewRemark", "");
        
        Map<String, Object> result = checkinService.approveCheckin(recordId, status, reviewRemark);
        return Result.success(result);
    }
    
    /**
     * 获取待审核记录（管理员功能）
     */
    @GetMapping("/pending")
    public Result<Page<CheckinRecord>> getPendingRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<CheckinRecord> records = checkinService.getPendingRecords(page, pageSize);
        return Result.success(records);
    }
}
