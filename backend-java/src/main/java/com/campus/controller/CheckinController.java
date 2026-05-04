package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.CheckinRecord;
import com.campus.service.CheckinService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return Long.parseLong(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return currentUserId(authentication) == 0L;
    }
    
    /**
     * 提交晚寝签到
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitCheckin(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String location = "";
        Object dormObj = request.get("dorm");
        if (dormObj instanceof String && !((String) dormObj).isBlank()) {
            location = (String) dormObj;
        } else {
            Object locationObj = request.get("location");
            if (locationObj instanceof String) {
                location = (String) locationObj;
            } else if (locationObj instanceof Map<?, ?> locationMap) {
                Object lat = locationMap.get("latitude");
                Object lng = locationMap.get("longitude");
                location = String.format("定位坐标: %s, %s",
                    lat != null ? lat : "-",
                    lng != null ? lng : "-");
            }
        }
        if (location.isBlank()) {
            location = "未提供具体位置";
        }

        String remark = request.get("remark") instanceof String ? (String) request.get("remark") : "";
        
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
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            return Result.error(403, "无管理员权限");
        }
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
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            return Result.error(403, "无管理员权限");
        }
        Page<CheckinRecord> records = checkinService.getPendingRecords(page, pageSize);
        return Result.success(records);
    }

    /**
     * 管理员：查询全部签到记录
     */
    @GetMapping("/admin/records")
    public Result<Page<CheckinRecord>> getAdminRecords(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Page<CheckinRecord> records = checkinService.getAdminRecords(
                operatorUserId, status, keyword, page, pageSize);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：强制修改签到状态
     */
    @PostMapping("/admin/records/{recordId}/status")
    public Result<Map<String, Object>> adminForceStatus(
            @PathVariable Long recordId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            String status = request.get("status");
            String reviewRemark = request.getOrDefault("reviewRemark", "");
            Map<String, Object> result = checkinService.adminForceStatus(
                operatorUserId, recordId, status, reviewRemark);
            return Result.success("状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 兼容旧版前端 - 获取签到状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> status = checkinService.getTodayStatus(userId);
        return Result.success(status);
    }

    /**
     * 兼容旧版前端 - 获取签到历史
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Page<CheckinRecord> records = checkinService.getCheckinRecords(userId, page, pageSize);

        Map<String, Object> data = new HashMap<>();
        data.put("list", records.getRecords());
        data.put("total", records.getTotal());
        data.put("page", records.getCurrent());
        data.put("pageSize", records.getSize());
        return Result.success(data);
    }
}
