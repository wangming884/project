package com.campus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.entity.SecondhandProduct;
import com.campus.service.SecondhandService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return Long.parseLong(authentication.getName());
    }

    private Map<String, Object> toLegacyProduct(SecondhandProduct product) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", product.getId());
        item.put("title", product.getTitle());
        item.put("price", product.getPrice());
        item.put("seller", product.getSellerName());
        item.put("createdAt", product.getCreatedAt());
        item.put("category", product.getCategory());

        String image = "";
        if (product.getImages() != null && !product.getImages().isBlank()) {
            image = product.getImages().split(",")[0].trim();
        }
        item.put("image", image);
        return item;
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

    /**
     * 兼容旧版前端 - 商品列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> listCompat(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<SecondhandProduct> pageInfo = secondhandService.getProducts(category, null, "latest", page, pageSize);

        List<Map<String, Object>> list = new ArrayList<>();
        for (SecondhandProduct product : pageInfo.getRecords()) {
            list.add(toLegacyProduct(product));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", pageInfo.getTotal());
        data.put("page", pageInfo.getCurrent());
        data.put("pageSize", pageInfo.getSize());
        return Result.success(data);
    }

    /**
     * 兼容旧版前端 - 搜索商品
     */
    @GetMapping("/search")
    public Result<Map<String, Object>> searchCompat(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<SecondhandProduct> pageInfo = secondhandService.getProducts(category, keyword, "latest", page, pageSize);

        List<Map<String, Object>> list = new ArrayList<>();
        for (SecondhandProduct product : pageInfo.getRecords()) {
            list.add(toLegacyProduct(product));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", pageInfo.getTotal());
        return Result.success(data);
    }

    /**
     * 兼容旧版前端 - 联系卖家
     */
    @PostMapping("/contact")
    public Result<Map<String, Object>> contactCompat(@RequestBody Map<String, Object> request) {
        if (request.get("productId") == null) {
            return Result.error("productId 不能为空");
        }

        Long productId = Long.valueOf(request.get("productId").toString());
        Map<String, Object> detail = secondhandService.getProductDetail(productId);
        SecondhandProduct product = (SecondhandProduct) detail.get("product");
        if (product == null) {
            return Result.error("商品不存在");
        }

        String contact = product.getContact();
        if (contact == null || contact.isBlank()) {
            contact = "请通过站内私信联系卖家";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("contact", contact);
        data.put("seller", product.getSellerName());
        return Result.success(data);
    }

    /**
     * 管理员：查询全量商品（可按状态筛选）
     */
    @GetMapping("/admin/products")
    public Result<Page<SecondhandProduct>> adminGetProducts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Page<SecondhandProduct> products = secondhandService.adminGetProducts(
                operatorUserId, status, keyword, page, pageSize);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：强制修改商品状态
     */
    @PostMapping("/admin/products/{productId}/status")
    public Result<Map<String, Object>> adminForceStatus(
            @PathVariable Long productId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            String status = request.get("status");
            Map<String, Object> result = secondhandService.adminForceStatus(operatorUserId, productId, status);
            return Result.success("状态更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员：强制删除商品
     */
    @DeleteMapping("/admin/products/{productId}")
    public Result<Map<String, Object>> adminForceDelete(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            Long operatorUserId = currentUserId(authentication);
            Map<String, Object> result = secondhandService.adminForceDelete(operatorUserId, productId);
            return Result.success("删除成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
