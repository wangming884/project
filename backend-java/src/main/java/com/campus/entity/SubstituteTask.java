package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代课任务实体
 * 
 * @author Campus Platform Team
 */
@Data
@TableName("substitute_tasks")
public class SubstituteTask {
    
    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 课程名称
     */
    private String course;
    
    /**
     * 上课时间
     */
    private LocalDateTime time;
    
    /**
     * 上课地点
     */
    private String location;
    
    /**
     * 酬金
     */
    private String reward;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 发布者ID
     */
    private Long publisherId;
    
    /**
     * 发布者姓名
     */
    private String publisherName;
    
    /**
     * 接单者ID
     */
    private Long accepterId;
    
    /**
     * 接单者姓名
     */
    private String accepterName;
    
    /**
     * 状态（pending-待接单，accepted-已接单，completed-已完成，cancelled-已取消）
     */
    private String status;
    
    /**
     * 逻辑删除标记
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
