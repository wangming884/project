package com.campus.common.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 * 
 * @author Campus Platform Team
 */
@Getter
public enum UserStatus {
    
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    
    /**
     * 禁用
     */
    DISABLED(1, "禁用"),
    
    /**
     * 已删除
     */
    DELETED(2, "已删除");
    
    private final Integer code;
    private final String description;
    
    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static UserStatus fromCode(Integer code) {
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid user status code: " + code);
    }
}
