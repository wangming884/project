package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.AnnouncementService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 前台：查询公告
     */
    @GetMapping("/latest")
    public Result<Map<String, Object>> latest(@RequestParam(defaultValue = "all") String scope) {
        try {
            return Result.success(announcementService.latest(scope));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：查询公告列表
     */
    @GetMapping("/admin/list")
    public Result<Map<String, Object>> adminList(Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            return Result.success(announcementService.adminList(operatorUserId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：发布公告
     */
    @PostMapping("/admin/publish")
    public Result<Map<String, Object>> publish(
            @RequestBody PublishRequest request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Map<String, Object> result = announcementService.publish(
                operatorUserId,
                request.getScope(),
                request.getTitle(),
                request.getContent()
            );
            return Result.success("公告发布成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    static class PublishRequest {
        private String scope;
        private String title;
        private String content;
    }
}
