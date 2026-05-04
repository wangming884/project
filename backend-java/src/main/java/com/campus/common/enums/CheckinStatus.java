package com.campus.common.enums;

import lombok.Getter;

/**
 * 签到状态枚举
 * 
 * @author Campus Platform Team
 */
@Getter
public enum CheckinStatus {
    
    /**
     * 待审核
     */
    PENDING("pending", "待审核"),
    
    /**
     * 已通过
     */
    APPROVED("approved", "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED("rejected", "已拒绝");
    
    private final String code;
    private final String description;
    
    CheckinStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static CheckinStatus fromCode(String code) {
        for (CheckinStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid checkin status code: " + code);
    }
}
