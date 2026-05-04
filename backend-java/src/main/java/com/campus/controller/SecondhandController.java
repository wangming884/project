package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.SecondhandProduct;
import com.campus.service.SecondhandService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 二手交易控制器
 * 
 * @author Campus Platform Team
 */
@RestController
@RequestMapping("/secondhand")
public class SecondhandController {
    
    private final SecondhandService secondhandService;
    
    public SecondhandController(SecondhandService secondhandService) {
        this.secondhandService = secondhandService;
    }
    
    /**
     * 发布商品
     */
    @PostMapping("/publish")
    public Result<Map<String, Object>> publishProduct(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        String images = (String) request.getOrDefault("images", "");
        
        Map<String, Object> result = secondhandService.publishProduct(
            userId, title, description, price, category, images);
        return Result.success(result);
    }
    
    /**
     * 获取商品列表
     */
    @GetMapping("/products")
    public Result<Page<SecondhandProduct>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SecondhandProduct> products = secondhandService.getProducts(
            category, keyword, sortBy, page, pageSize);
        return Result.success(products);
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/products/{productId}")
    public Result<Map<String, Object>> getProductDetail(@PathVariable Long productId) {
        Map<String, Object> detail = secondhandService.getProductDetail(productId);
        return Result.success(detail);
    }
    
    /**
     * 获取我的发布
     */
    @GetMapping("/my-products")
    public Result<Page<SecondhandProduct>> getMyProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Page<SecondhandProduct> products = secondhandService.getMyProducts(userId, page, pageSize);
        return Result.success(products);
    }
    
    /**
     * 更新商品信息
     */
    @PutMapping("/products/{productId}")
    public Result<Map<String, Object>> updateProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        BigDecimal price = request.get("price") != null 
            ? new BigDecimal(request.get("price").toString()) : null;
        String category = (String) request.get("category");
        
        Map<String, Object> result = secondhandService.updateProduct(
            productId, userId, title, description, price, category);
        return Result.success(result);
    }
    
    /**
     * 更新商品状态
     */
    @PatchMapping("/products/{productId}/status")
    public Result<Map<String, Object>> updateStatus(
            @PathVariable Long productId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String status = request.get("status");
        
        Map<String, Object> result = secondhandService.updateStatus(productId, userId, status);
        return Result.success(result);
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/products/{productId}")
    public Result<Map<String, Object>> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> result = secondhandService.deleteProduct(productId, userId);
        return Result.success(result);
    }
    
    /**
     * 获取商品统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> statistics = secondhandService.getStatistics(userId);
        return Result.success(statistics);
    }
}
