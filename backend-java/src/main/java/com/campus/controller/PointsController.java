package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.PointsHistory;
import com.campus.entity.User;
import com.campus.service.PointsService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 管理员：用户列表
     */
    @GetMapping("/admin/users")
    public Result<Map<String, Object>> listUsersForAdmin(
            Authentication authentication,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Long operatorUserId = (Long) authentication.getPrincipal();
            if (operatorUserId == null || operatorUserId != 0L) {
                return Result.error(403, "无管理员权限");
            }

            Page<User> pageInfo = pointsService.listUsersForAdmin(keyword, page, pageSize);

            List<Map<String, Object>> list = new ArrayList<>();
            for (User user : pageInfo.getRecords()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", user.getId());
                item.put("username", user.getUsername());
                item.put("email", user.getEmail());
                item.put("points", user.getPoints());
                item.put("status", user.getStatus());
                item.put("lastSignInDate", user.getLastSignInDate());
                item.put("continuousDays", user.getContinuousDays());
                item.put("createdAt", user.getCreatedAt());
                list.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", pageInfo.getTotal());
            data.put("page", pageInfo.getCurrent());
            data.put("pageSize", pageInfo.getSize());
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：积分增减
     */
    @PostMapping("/admin/adjust")
    public Result<Map<String, Object>> adminAdjustPoints(
            Authentication authentication,
            @RequestBody AdminAdjustRequest request) {
        try {
            Long operatorUserId = (Long) authentication.getPrincipal();
            Map<String, Object> result = pointsService.adminAdjustPoints(
                operatorUserId,
                request.getUserId(),
                request.getDelta(),
                request.getReason()
            );
            return Result.success("积分调整成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：启用/禁用账号
     */
    @PostMapping("/admin/users/{userId}/status")
    public Result<Map<String, Object>> adminUpdateUserStatus(
            Authentication authentication,
            @PathVariable Long userId,
            @RequestBody AdminUserStatusRequest request) {
        try {
            Long operatorUserId = (Long) authentication.getPrincipal();
            Map<String, Object> result = pointsService.adminUpdateUserStatus(
                operatorUserId,
                userId,
                request.getStatus(),
                request.getReason()
            );
            return Result.success("账号状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：重置用户签到信息
     */
    @PostMapping("/admin/users/{userId}/reset-signin")
    public Result<Map<String, Object>> adminResetSignIn(
            Authentication authentication,
            @PathVariable Long userId,
            @RequestBody(required = false) AdminReasonRequest request) {
        try {
            Long operatorUserId = (Long) authentication.getPrincipal();
            String reason = request == null ? null : request.getReason();
            Map<String, Object> result = pointsService.adminResetUserSignIn(
                operatorUserId,
                userId,
                reason
            );
            return Result.success("签到信息重置成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：查询积分流水
     */
    @GetMapping("/admin/history")
    public Result<Map<String, Object>> adminGetHistory(
            Authentication authentication,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Long operatorUserId = (Long) authentication.getPrincipal();
            Page<PointsHistory> pageInfo = pointsService.adminGetHistory(
                operatorUserId, userId, type, page, pageSize);

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

    @Data
    static class AdminAdjustRequest {
        private Long userId;
        private Integer delta;
        private String reason;

        public int getDelta() {
            return delta == null ? 0 : delta;
        }
    }

    @Data
    static class AdminUserStatusRequest {
        private Integer status;
        private String reason;
    }

    @Data
    static class AdminReasonRequest {
        private String reason;
    }
}
