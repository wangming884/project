package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.PointsHistory;
import com.campus.service.PointsService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 积分控制器
 * 
 * @author Campus Platform Team
 */
@RestController
@RequestMapping("/points")
public class PointsController {
    
    private final PointsService pointsService;
    
    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }
    
    /**
     * 获取积分余额
     */
    @GetMapping("/balance")
    public Result<Map<String, Object>> getBalance(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Map<String, Object> data = pointsService.getBalance(userId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 每日签到
     */
    @PostMapping("/sign-in")
    public Result<Map<String, Object>> signIn(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Map<String, Object> data = pointsService.dailySignIn(userId);
            return Result.success("签到成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 兑换积分码
     */
    @PostMapping("/redeem")
    public Result<Map<String, Object>> redeem(Authentication authentication, 
                                               @RequestBody RedeemRequest request) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Map<String, Object> data = pointsService.redeem(userId, request.getCode());
            return Result.success("兑换成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 积分历史记录
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(Authentication authentication,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Page<PointsHistory> pageInfo = pointsService.getHistory(userId, page, pageSize);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", pageInfo.getRecords());
            data.put("total", pageInfo.getTotal());
            data.put("page", pageInfo.getCurrent());
            data.put("pageSize", pageInfo.getSize());
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // ==================== 请求对象 ====================
    
    @Data
    static class RedeemRequest {
        private String code;
    }
}
