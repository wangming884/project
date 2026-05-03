package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 晚寝签到记录实体类
 * 
 * @author Campus Platform Team
 */
@Data
@TableName("checkin_records")
public class CheckinRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 签到ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 学号/账号
     */
    private String account;
    
    /**
     * 寝室地址
     */
    private String dorm;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 签到时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
