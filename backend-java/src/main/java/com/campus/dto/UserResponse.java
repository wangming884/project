package com.campus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO
 * 
 * @author Campus Platform Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;
    
    /**
     * 最后签到时间
     */
    private LocalDateTime lastSignInTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
