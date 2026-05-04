package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.RecommendService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 站长好物控制器
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
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

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(recommendService.listPublicProducts(keyword, page, pageSize));
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam Long id) {
        return Result.success(recommendService.getDetail(id));
    }

    @PostMapping("/coupon")
    public Result<Map<String, Object>> coupon(@RequestBody CouponRequest request) {
        return Result.success(recommendService.getCoupon(request.getProductId()));
    }

    /**
     * 管理员：查询全量好物（含下架）
     */
    @GetMapping("/admin/list")
    public Result<Map<String, Object>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {
        try {
            Long userId = currentUserId(authentication);
            if (userId == null || userId != 0L) {
                return Result.error(403, "无管理员权限");
            }
            return Result.success(recommendService.listProducts(keyword, status, page, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：上架新好物
     */
    @PostMapping("/admin/create")
    public Result<Map<String, Object>> adminCreate(
            @RequestBody AdminCreateRequest request,
            Authentication authentication) {
        try {
            Long userId = currentUserId(authentication);
            Map<String, Object> result = recommendService.adminCreateProduct(
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getOriginalPrice(),
                request.getImageEmoji(),
                request.getBadge(),
                request.getBuyUrl()
            );
            return Result.success("上架成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：上架/下架
     */
    @PostMapping("/admin/{productId}/status")
    public Result<Map<String, Object>> adminStatus(
            @PathVariable Long productId,
            @RequestBody AdminStatusRequest request,
            Authentication authentication) {
        try {
            Long userId = currentUserId(authentication);
            Map<String, Object> result = recommendService.adminUpdateStatus(userId, productId, request.getStatus());
            return Result.success("状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    static class CouponRequest {
        private Long productId;
    }

    @Data
    static class AdminCreateRequest {
        private String title;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String imageEmoji;
        private String badge;
        private String buyUrl;
    }

    @Data
    static class AdminStatusRequest {
        private String status;
    }
}
