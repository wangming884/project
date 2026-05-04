package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.SubstituteTask;
import com.campus.service.SubstituteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 代课平台控制器
 * 
 * @author Campus Platform Team
 */
@RestController
@RequestMapping("/substitute")
public class SubstituteController {
    
    private final SubstituteService substituteService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public SubstituteController(SubstituteService substituteService) {
        this.substituteService = substituteService;
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return Long.parseLong(authentication.getName());
    }
    
    /**
     * 发布代课任务
     */
    @PostMapping("/publish")
    public Result<Map<String, Object>> publishTask(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String title = request.get("title");
        String course = request.get("course");
        LocalDateTime time = LocalDateTime.parse(request.get("time"), formatter);
        String location = request.get("location");
        String reward = request.get("reward");
        String description = request.getOrDefault("description", "");
        
        Map<String, Object> result = substituteService.publishTask(
            userId, title, course, time, location, reward, description);
        return Result.success(result);
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping("/tasks")
    public Result<Page<SubstituteTask>> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SubstituteTask> tasks = substituteService.getTasks(
            status, keyword, sortBy, page, pageSize);
        return Result.success(tasks);
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public Result<Map<String, Object>> getTaskDetail(@PathVariable Long taskId) {
        Map<String, Object> detail = substituteService.getTaskDetail(taskId);
        return Result.success(detail);
    }
    
    /**
     * 接单
     */
    @PostMapping("/tasks/{taskId}/accept")
    public Result<Map<String, Object>> acceptTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> result = substituteService.acceptTask(taskId, userId);
        return Result.success(result);
    }
    
    /**
     * 取消接单
     */
    @PostMapping("/tasks/{taskId}/cancel-accept")
    public Result<Map<String, Object>> cancelAccept(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> result = substituteService.cancelAccept(taskId, userId);
        return Result.success(result);
    }
    
    /**
     * 完成任务
     */
    @PostMapping("/tasks/{taskId}/complete")
    public Result<Map<String, Object>> completeTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> result = substituteService.completeTask(taskId, userId);
        return Result.success(result);
    }
    
    /**
     * 取消任务
     */
    @PostMapping("/tasks/{taskId}/cancel")
    public Result<Map<String, Object>> cancelTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> result = substituteService.cancelTask(taskId, userId);
        return Result.success(result);
    }
    
    /**
     * 获取我发布的任务
     */
    @GetMapping("/my-published")
    public Result<Page<SubstituteTask>> getMyPublishedTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Page<SubstituteTask> tasks = substituteService.getMyPublishedTasks(userId, page, pageSize);
        return Result.success(tasks);
    }
    
    /**
     * 获取我接的任务
     */
    @GetMapping("/my-accepted")
    public Result<Page<SubstituteTask>> getMyAcceptedTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Page<SubstituteTask> tasks = substituteService.getMyAcceptedTasks(userId, page, pageSize);
        return Result.success(tasks);
    }
    
    /**
     * 获取任务统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> statistics = substituteService.getStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 管理员：查询全量任务
     */
    @GetMapping("/admin/tasks")
    public Result<Page<SubstituteTask>> adminGetTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            if (operatorUserId != 0L) {
                return Result.error(403, "无管理员权限");
            }
            Page<SubstituteTask> tasks = substituteService.getTasks(status, keyword, sortBy, page, pageSize);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：强制修改任务状态
     */
    @PostMapping("/admin/tasks/{taskId}/status")
    public Result<Map<String, Object>> adminForceStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            String status = request.get("status");
            Map<String, Object> result = substituteService.adminForceStatus(operatorUserId, taskId, status);
            return Result.success("任务状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
