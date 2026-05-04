package com.campus.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 站长好物商品（内存实现）
 */
@Data
public class RecommendProduct {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String imageEmoji;

    private String badge;

    private String buyUrl;

    /**
     * 上架状态：on / off
     */
    private String status;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
