package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author Campus Platform Team
 */
@Data
@TableName("users")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
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
     * 密码（加密后）
     */
    private String password;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 积分余额
     */
    private Integer points;
    
    /**
     * 最后签到日期
     */
    private String lastSignInDate;
    
    /**
     * 连续签到天数
     */
    private Integer continuousDays;
    
    /**
     * 账号状态（0-禁用，1-正常）
     */
    private Integer status;
    
    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
