package com.campus.service;

import com.campus.entity.RecommendProduct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 站长好物服务（当前为内存存储实现）
 */
@Service
public class RecommendService {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final Map<Long, RecommendProduct> productStore = new ConcurrentHashMap<>();

    public RecommendService() {
        seedData();
    }

    private void assertAdmin(Long operatorUserId) {
        if (operatorUserId == null || operatorUserId != 0L) {
            throw new RuntimeException("无管理员权限");
        }
    }

    public Map<String, Object> listProducts(String keyword, String status, int page, int pageSize) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        String s = status == null ? "" : status.trim().toLowerCase();

        List<RecommendProduct> all = productStore.values().stream()
            .filter(item -> q.isBlank()
                || safe(item.getTitle()).toLowerCase().contains(q)
                || safe(item.getDescription()).toLowerCase().contains(q))
            .filter(item -> s.isBlank() || safe(item.getStatus()).equalsIgnoreCase(s))
            .sorted(Comparator.comparing(RecommendProduct::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<RecommendProduct> pageRows = from >= all.size() ? new ArrayList<>() : new ArrayList<>(all.subList(from, to));

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageRows);
        result.put("total", all.size());
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    public Map<String, Object> listPublicProducts(String keyword, int page, int pageSize) {
        return listProducts(keyword, "on", page, pageSize);
    }

    public Map<String, Object> getDetail(Long productId) {
        RecommendProduct item = productStore.get(productId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("product", item);
        return result;
    }

    public Map<String, Object> getCoupon(Long productId) {
        RecommendProduct item = productStore.get(productId);
        if (item == null || !"on".equalsIgnoreCase(item.getStatus())) {
            throw new RuntimeException("商品不存在或未上架");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("productId", item.getId());
        result.put("couponCode", "COUPON-" + item.getId() + "-" + System.currentTimeMillis());
        result.put("message", "优惠券已生成，请跳转外部站点使用");
        result.put("buyUrl", safe(item.getBuyUrl()));
        return result;
    }

    public Map<String, Object> adminCreateProduct(
        Long operatorUserId,
        String title,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        String imageEmoji,
        String badge,
        String buyUrl
    ) {
        assertAdmin(operatorUserId);
        if (title == null || title.isBlank()) {
            throw new RuntimeException("商品标题不能为空");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("商品价格必须大于0");
        }

        RecommendProduct item = new RecommendProduct();
        item.setId(idGenerator.incrementAndGet());
        item.setTitle(title.trim());
        item.setDescription(description == null ? "" : description.trim());
        item.setPrice(price);
        item.setOriginalPrice(originalPrice == null ? price : originalPrice);
        item.setImageEmoji(imageEmoji == null || imageEmoji.isBlank() ? "🎁" : imageEmoji.trim());
        item.setBadge(badge == null ? "" : badge.trim());
        item.setBuyUrl(buyUrl == null ? "" : buyUrl.trim());
        item.setStatus("on");
        item.setCreatedBy(operatorUserId);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        productStore.put(item.getId(), item);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", item.getId());
        result.put("title", item.getTitle());
        result.put("status", item.getStatus());
        return result;
    }

    public Map<String, Object> adminUpdateStatus(Long operatorUserId, Long productId, String status) {
        assertAdmin(operatorUserId);
        if (!Objects.equals("on", status) && !Objects.equals("off", status)) {
            throw new RuntimeException("状态仅支持 on / off");
        }
        RecommendProduct item = productStore.get(productId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        item.setStatus(status);
        item.setUpdatedAt(LocalDateTime.now());
        productStore.put(productId, item);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", item.getId());
        result.put("status", item.getStatus());
        return result;
    }

    private void seedData() {
        addSeed("非对称光源屏幕挂灯", "宿舍熄灯后熬夜赶Due必备，非对称光线设计不反光。", "69.00", "129.00", "💡", "爆款推荐", "https://example.com/product/lamp");
        addSeed("太空记忆棉护腰靠垫", "拯救久坐党，人体工学设计，贴合腰椎曲线。", "45.00", "88.00", "💺", "", "https://example.com/product/backrest");
        addSeed("零压感隔音睡眠耳塞", "宿舍防噪音神器，慢回弹海绵材质。", "9.90", "19.90", "🎧", "", "https://example.com/product/earplug");
    }

    private void addSeed(String title, String desc, String p, String op, String emoji, String badge, String buyUrl) {
        RecommendProduct item = new RecommendProduct();
        item.setId(idGenerator.incrementAndGet());
        item.setTitle(title);
        item.setDescription(desc);
        item.setPrice(new BigDecimal(p));
        item.setOriginalPrice(new BigDecimal(op));
        item.setImageEmoji(emoji);
        item.setBadge(badge);
        item.setBuyUrl(buyUrl);
        item.setStatus("on");
        item.setCreatedBy(0L);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        productStore.put(item.getId(), item);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
