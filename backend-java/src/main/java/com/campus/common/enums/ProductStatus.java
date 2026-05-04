package com.campus.common.enums;

import lombok.Getter;

/**
 * 商品状态枚举
 * 
 * @author Campus Platform Team
 */
@Getter
public enum ProductStatus {
    
    /**
     * 可售
     */
    AVAILABLE("available", "可售"),
    
    /**
     * 已售
     */
    SOLD("sold", "已售"),
    
    /**
     * 已下架
     */
    OFF_SHELF("off_shelf", "已下架");
    
    private final String code;
    private final String description;
    
    ProductStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static ProductStatus fromCode(String code) {
        for (ProductStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid product status code: " + code);
    }
}
