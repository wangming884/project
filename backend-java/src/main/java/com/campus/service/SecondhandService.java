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
import java.util.Set;

/**
 * 二手交易服务
 *
 * @author Campus Platform Team
 */
@Service
public class SecondhandService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("books", "electronics", "daily", "transport");
    private static final Set<String> ALLOWED_STATUSES = Set.of("available", "sold", "removed");
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final SecondhandProductMapper productMapper;
    private final UserMapper userMapper;

    public SecondhandService(SecondhandProductMapper productMapper, UserMapper userMapper) {
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }

    private boolean isAdmin(Long userId) {
        return userId != null && userId == 0L;
    }

    private String normalizeRequiredText(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new RuntimeException(fieldName + "不能为空");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("价格必须大于 0");
        }
        return price.stripTrailingZeros();
    }

    private String normalizeCategory(String category) {
        String normalized = normalizeRequiredText(category, "商品分类");
        if (!ALLOWED_CATEGORIES.contains(normalized)) {
            throw new RuntimeException("商品分类仅支持 books / electronics / daily / transport");
        }
        return normalized;
    }

    private String normalizeOptionalCategory(String category) {
        String normalized = normalizeOptionalText(category);
        if (normalized.isEmpty()) {
            return null;
        }
        return normalizeCategory(normalized);
    }

    private String normalizeStatus(String status) {
        String normalized = normalizeRequiredText(status, "状态");
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new RuntimeException("状态仅支持 available / sold / removed");
        }
        return normalized;
    }

    private String normalizeKeyword(String keyword) {
        String normalized = normalizeOptionalText(keyword);
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeSortBy(String sortBy) {
        String normalized = normalizeOptionalText(sortBy);
        if (normalized.isEmpty()) {
            return "latest";
        }
        if ("latest".equals(normalized) || "price_asc".equals(normalized)
            || "price_desc".equals(normalized) || "views".equals(normalized)) {
            return normalized;
        }
        return "latest";
    }

    private int normalizePage(int page) {
        return page > 0 ? page : DEFAULT_PAGE;
    }

    private int normalizePageSize(int pageSize) {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private SecondhandProduct requireExistingProduct(Long productId) {
        SecondhandProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return product;
    }

    /**
     * 发布商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> publishProduct(Long userId, String title, String description,
                                              BigDecimal price, String category, String images, String contact) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        SecondhandProduct product = new SecondhandProduct();
        product.setSellerId(userId);
        product.setSellerName(user.getUsername());
        product.setTitle(normalizeRequiredText(title, "商品标题"));
        product.setDescription(normalizeOptionalText(description));
        product.setPrice(normalizePrice(price));
        product.setCategory(normalizeCategory(category));
        product.setImages(normalizeOptionalText(images));
        product.setContact(normalizeRequiredText(contact, "联系方式"));
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
        String normalizedCategory = normalizeOptionalCategory(category);
        String normalizedKeyword = normalizeKeyword(keyword);
        String normalizedSortBy = normalizeSortBy(sortBy);
        Page<SecondhandProduct> pageInfo = new Page<>(normalizePage(page), normalizePageSize(pageSize));

        LambdaQueryWrapper<SecondhandProduct> query = new LambdaQueryWrapper<>();
        query.eq(SecondhandProduct::getStatus, "available");

        if (normalizedCategory != null) {
            query.eq(SecondhandProduct::getCategory, normalizedCategory);
        }

        if (normalizedKeyword != null) {
            query.and(wrapper -> wrapper
                .like(SecondhandProduct::getTitle, normalizedKeyword)
                .or()
                .like(SecondhandProduct::getDescription, normalizedKeyword)
            );
        }

        if ("price_asc".equals(normalizedSortBy)) {
            query.orderByAsc(SecondhandProduct::getPrice);
        } else if ("price_desc".equals(normalizedSortBy)) {
            query.orderByDesc(SecondhandProduct::getPrice);
        } else if ("views".equals(normalizedSortBy)) {
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
        SecondhandProduct product = requireExistingProduct(productId);

        product.setViews((product.getViews() == null ? 0 : product.getViews()) + 1);
        productMapper.updateById(product);

        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        return result;
    }

    /**
     * 获取用于联系卖家的商品信息，不增加浏览量
     */
    public SecondhandProduct getProductContactInfo(Long productId) {
        return requireExistingProduct(productId);
    }

    /**
     * 获取我的发布
     */
    public Page<SecondhandProduct> getMyProducts(Long userId, int page, int pageSize) {
        Page<SecondhandProduct> pageInfo = new Page<>(normalizePage(page), normalizePageSize(pageSize));

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
                                             String description, BigDecimal price, String category, String contact) {
        SecondhandProduct product = requireExistingProduct(productId);

        if (!isAdmin(userId) && !product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权修改此商品");
        }

        if (title != null) {
            product.setTitle(normalizeRequiredText(title, "商品标题"));
        }
        if (description != null) {
            product.setDescription(normalizeOptionalText(description));
        }
        if (price != null) {
            product.setPrice(normalizePrice(price));
        }
        if (category != null) {
            product.setCategory(normalizeCategory(category));
        }
        if (contact != null) {
            product.setContact(normalizeRequiredText(contact, "联系方式"));
        }

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
        SecondhandProduct product = requireExistingProduct(productId);

        if (!isAdmin(userId) && !product.getSellerId().equals(userId)) {
            throw new RuntimeException("无权修改此商品");
        }

        product.setStatus(normalizeStatus(status));
        productMapper.updateById(product);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("status", product.getStatus());
        return result;
    }

    /**
     * 删除商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteProduct(Long productId, Long userId) {
        SecondhandProduct product = requireExistingProduct(productId);

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

    /**
     * 管理员查询商品（可按状态筛选，默认全量）
     */
    public Page<SecondhandProduct> adminGetProducts(Long operatorUserId, String status, String keyword, int page, int pageSize) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }

        String normalizedStatus = normalizeOptionalText(status);
        String normalizedKeyword = normalizeKeyword(keyword);
        Page<SecondhandProduct> pageInfo = new Page<>(normalizePage(page), normalizePageSize(pageSize));
        LambdaQueryWrapper<SecondhandProduct> query = new LambdaQueryWrapper<>();

        if (!normalizedStatus.isEmpty()) {
            query.eq(SecondhandProduct::getStatus, normalizeStatus(normalizedStatus));
        }
        if (normalizedKeyword != null) {
            query.and(wrapper -> wrapper
                .like(SecondhandProduct::getTitle, normalizedKeyword)
                .or()
                .like(SecondhandProduct::getDescription, normalizedKeyword)
                .or()
                .like(SecondhandProduct::getSellerName, normalizedKeyword)
            );
        }
        query.orderByDesc(SecondhandProduct::getCreatedAt);
        return productMapper.selectPage(pageInfo, query);
    }

    /**
     * 管理员强制修改商品状态
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminForceStatus(Long operatorUserId, Long productId, String status) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }

        SecondhandProduct product = requireExistingProduct(productId);
        product.setStatus(normalizeStatus(status));
        productMapper.updateById(product);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("status", product.getStatus());
        return result;
    }

    /**
     * 管理员强制删除商品
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminForceDelete(Long operatorUserId, Long productId) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }

        requireExistingProduct(productId);
        productMapper.deleteById(productId);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("message", "管理员删除成功");
        return result;
    }
}
