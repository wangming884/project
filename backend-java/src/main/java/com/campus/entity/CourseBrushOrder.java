package com.campus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代刷课订单实体
 */
@Data
@TableName("course_brush_orders")
public class CourseBrushOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Long courseId;

    private String courseName;

    private Integer requiredPoints;

    private String studentAccount;

    private String studentPassword;

    private String remark;

    private String status;

    private String resultMessage;

    private String scriptName;

    private String requestId;

    private String source;

    private LocalDateTime submittedAt;

    private LocalDateTime processedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
