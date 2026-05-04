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
     * 用户名
     */
    private String username;
    
    /**
     * 签到位置
     */
    private String location;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;
    
    /**
     * 状态（pending-待审核，approved-已通过，rejected-已拒绝）
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 审核备注
     */
    private String reviewRemark;
    
    /**
     * 纬度（可选）
     */
    private BigDecimal latitude;
    
    /**
     * 经度（可选）
     */
    private BigDecimal longitude;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
