package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.CourseBrushCourse;
import com.campus.entity.CourseBrushOrder;
import com.campus.service.CourseBrushService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代刷课控制器
 */
@RestController
@RequestMapping("/course-brush")
public class CourseBrushController {

    private final CourseBrushService courseBrushService;

    @Value("${automation-course-brush.enabled:false}")
    private boolean automationEnabled;

    @Value("${automation-course-brush.script-key:}")
    private String automationScriptKey;

    public CourseBrushController(CourseBrushService courseBrushService) {
        this.courseBrushService = courseBrushService;
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return Long.parseLong(authentication.getName());
    }

    private boolean validAutomationKey(String input) {
        return automationScriptKey != null
            && !automationScriptKey.isBlank()
            && automationScriptKey.equals(input);
    }

    @GetMapping("/courses")
    public Result<Page<CourseBrushCourse>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {
        return Result.success(courseBrushService.getCourses(true, keyword, page, pageSize));
    }

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitOrder(
            @RequestBody SubmitOrderRequest request,
            Authentication authentication) {
        Long userId = currentUserId(authentication);
        Map<String, Object> result = courseBrushService.submitOrder(
            userId,
            request.getCourseId(),
            request.getStudentAccount(),
            request.getStudentPassword(),
            request.getRemark()
        );
        return Result.success("代刷课订单提交成功", result);
    }

    @GetMapping("/my-orders")
    public Result<Page<CourseBrushOrder>> getMyOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {
        Long userId = currentUserId(authentication);
        return Result.success(courseBrushService.getMyOrders(userId, status, page, pageSize));
    }

    @GetMapping("/admin/courses")
    public Result<Page<CourseBrushCourse>> adminGetCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            Authentication authentication) {
        try {
            if (currentUserId(authentication) != 0L) {
                return Result.error(403, "无管理员权限");
            }
            return Result.success(courseBrushService.getCourses(false, keyword, page, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/admin/courses")
    public Result<Map<String, Object>> adminSaveCourse(
            @RequestBody SaveCourseRequest request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Map<String, Object> result = courseBrushService.adminSaveCourse(
                operatorUserId,
                request.getCourseId(),
                request.getCourseName(),
                request.getCourseCode(),
                request.getRequiredPoints(),
                request.getDescription(),
                request.getEnabled()
            );
            return Result.success("课程保存成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/admin/courses/{courseId}/status")
    public Result<Map<String, Object>> adminUpdateCourseStatus(
            @PathVariable Long courseId,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Map<String, Object> result = courseBrushService.adminUpdateCourseStatus(
                operatorUserId,
                courseId,
                request.get("enabled")
            );
            return Result.success("课程状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/admin/orders")
    public Result<Page<CourseBrushOrder>> adminGetOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            return Result.success(courseBrushService.getAdminOrders(operatorUserId, status, keyword, page, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/admin/orders/{orderId}/status")
    public Result<Map<String, Object>> adminUpdateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Map<String, Object> result = courseBrushService.adminUpdateOrderStatus(
                operatorUserId,
                orderId,
                request.getStatus(),
                request.getResultMessage()
            );
            return Result.success("订单状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/automation/spec")
    public Result<Map<String, Object>> automationSpec() {
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", automationEnabled);
        data.put("endpoint", "/api/course-brush/automation/submit");
        data.put("authHeader", "X-Automation-Key");
        data.put("method", "POST");
        data.put("workflow", "前台用户先提交订单并扣除积分，自动化脚本再回写订单处理状态。");
        data.put("requestBodyExample", Map.of(
            "orderId", 1001,
            "status", "completed",
            "resultMessage", "已完成本次课程处理",
            "scriptName", "course-brush-worker",
            "requestId", "brush-job-20260505-220000"
        ));
        data.put("message", "该接口为刷课脚本预留，用于后续自动化服务回写订单执行结果。");
        return Result.success(data);
    }

    @PostMapping("/automation/submit")
    public Result<Map<String, Object>> automationSubmit(
            @RequestBody AutomationSubmitRequest request,
            @RequestHeader(value = "X-Automation-Key", required = false) String scriptKey) {
        try {
            if (!automationEnabled) {
                return Result.error(503, "自动化刷课未启用，请先在配置中开启 automation-course-brush.enabled");
            }
            if (!validAutomationKey(scriptKey)) {
                return Result.error(401, "自动化脚本密钥无效");
            }
            if (request.getOrderId() == null || request.getOrderId() <= 0) {
                return Result.error("orderId 不合法");
            }

            Map<String, Object> result = courseBrushService.automationUpdateOrderStatus(
                request.getOrderId(),
                request.getStatus(),
                request.getResultMessage(),
                request.getScriptName(),
                request.getRequestId()
            );
            return Result.success("自动化脚本回写成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    static class SubmitOrderRequest {
        private Long courseId;
        private String studentAccount;
        private String studentPassword;
        private String remark;
    }

    @Data
    static class SaveCourseRequest {
        private Long courseId;
        private String courseName;
        private String courseCode;
        private Integer requiredPoints;
        private String description;
        private Integer enabled;
    }

    @Data
    static class UpdateOrderStatusRequest {
        private String status;
        private String resultMessage;
    }

    @Data
    static class AutomationSubmitRequest {
        private Long orderId;
        private String status;
        private String resultMessage;
        private String scriptName;
        private String requestId;
    }
}
