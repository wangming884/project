package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.SecondhandProduct;
import com.campus.entity.User;
import com.campus.mapper.SecondhandProductMapper;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 二手交易服务
 * 
 * @author Campus Platform Team
 */
@Service
public class SecondhandService {
    
    private final SecondhandProductMapper productMapper;
    private final UserMapper userMapper;
    
    public SecondhandService(SecondhandProductMapper productMapper, UserMapper userMapper) {
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    private boolean isAdmin(Long userId) {
        return userId != null && userId == 0L;
    }
    
    /**
     * 发布商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> publishProduct(Long userId, String title, String description, 
                                             BigDecimal price, String category, String images) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建商品
        SecondhandProduct product = new SecondhandProduct();
        product.setSellerId(userId);
        product.setSellerName(user.getUsername());
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImages(images);
        product.setStatus("available");
        product.setViews(0);
        
        productMapper.insert(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getId());
        result.put("title", product.getTitle());
        result.put("status", product.getStatus());
        
        return result;
    }
    
    /**
     * 获取商品列表
     */
    public Page<SecondhandProduct> getProducts(String category, String keyword, 
                                               String sortBy, int page, int pageSize) {
        Page<SecondhandProduct> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SecondhandProduct> query = new LambdaQueryWrapper<>();
        query.eq(SecondhandProduct::getStatus, "available");
        
        // 分类筛选
        if (category != null && !category.isEmpty()) {
            query.eq(SecondhandProduct::getCategory, category);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            query.and(wrapper -> wrapper
                .like(SecondhandProduct::getTitle, keyword)
                .or()
                .like(SecondhandProduct::getDescription, keyword)
            );
        }
        
        // 排序
        if ("price_asc".equals(sortBy)) {
            query.orderByAsc(SecondhandProduct::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            query.orderByDesc(SecondhandProduct::getPrice);
        } else if ("views".equals(sortBy)) {
            query.orderByDesc(SecondhandProduct::getViews);
        } else {
            query.orderByDesc(SecondhandProduct::getCreatedAt);
        }
        
        return productMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取商品详情
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getProductDetail(Long productId) {
        SecondhandProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 增加浏览次数
        product.setViews((product.getViews() == null ? 0 : product.getViews()) + 1);
        productMapper.updateById(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        
        return result;
    }
    
    /**
     * 获取我的发布
     */
    public Page<SecondhandProduct> getMyProducts(Long userId, int page, int pageSize) {
        Page<SecondhandProduct> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SecondhandProduct> query = new LambdaQueryWrapper<>();
        query.eq(SecondhandProduct::getSellerId, userId)
             .orderByDesc(SecondhandProduct::getCreatedAt);
        
        return productMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 更新商品信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProduct(Long productId, Long userId, String title, 
                                            String description, BigDecimal price, String category) {
        SecondhandProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        if (!isAdmin(userId) && !product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权修改此商品");
        }
        
        if (title != null) product.setTitle(title);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (category != null) product.setCategory(category);
        
        productMapper.updateById(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("message", "更新成功");
        
        return result;
    }
    
    /**
     * 更新商品状态
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateStatus(Long productId, Long userId, String status) {
        SecondhandProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        if (!isAdmin(userId) && !product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权修改此商品");
        }
        
        product.setStatus(status);
        productMapper.updateById(product);
        
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("status", status);
        
        return result;
    }
    
    /**
     * 删除商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteProduct(Long productId, Long userId) {
        SecondhandProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        if (!isAdmin(userId) && !product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权删除此商品");
        }
        
        productMapper.deleteById(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        
        return result;
    }
    
    /**
     * 获取商品统计
     */
    public Map<String, Object> getStatistics(Long userId) {
        LambdaQueryWrapper<SecondhandProduct> query = new LambdaQueryWrapper<>();
        query.eq(SecondhandProduct::getSellerId, userId);
        
        Long totalCount = productMapper.selectCount(query);
        
        query.eq(SecondhandProduct::getStatus, "available");
        Long availableCount = productMapper.selectCount(query);
        
        query.clear();
        query.eq(SecondhandProduct::getSellerId, userId)
             .eq(SecondhandProduct::getStatus, "sold");
        Long soldCount = productMapper.selectCount(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("availableCount", availableCount);
        result.put("soldCount", soldCount);
        
        return result;
    }
}
