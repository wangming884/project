package com.campus.common.enums;

import lombok.Getter;

/**
 * 积分类型枚举
 * 
 * @author Campus Platform Team
 */
@Getter
public enum PointsType {
    
    /**
     * 签到
     */
    SIGN_IN("sign_in", "每日签到"),
    
    /**
     * 兑换
     */
    REDEEM("redeem", "积分兑换"),
    
    /**
     * 任务奖励
     */
    TASK_REWARD("task_reward", "任务奖励"),
    
    /**
     * 系统赠送
     */
    SYSTEM_GIFT("system_gift", "系统赠送"),
    
    /**
     * 消费
     */
    CONSUME("consume", "积分消费"),
    
    /**
     * 退款
     */
    REFUND("refund", "积分退款");
    
    private final String code;
    private final String description;
    
    PointsType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static PointsType fromCode(String code) {
        for (PointsType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid points type code: " + code);
    }
}
