package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分历史记录实体类
 * 
 * @author Campus Platform Team
 */
@Data
@TableName("points_history")
public class PointsHistory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 类型（sign_in-签到，checkin-晚寝签到，redeem-兑换，purchase-购买）
     */
    private String type;
    
    /**
     * 积分变动数量（正数为增加，负数为减少）
     */
    private Integer amount;
    
    /**
     * 变动后余额
     */
    private Integer balance;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
