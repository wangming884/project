package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二手商品实体类
 * 
 * @author Campus Platform Team
 */
@Data
@TableName("secondhand_products")
public class SecondhandProduct implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 分类（books-书籍，electronics-电子，daily-日用，transport-交通）
     */
    private String category;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片URL（多张用逗号分隔）
     */
    private String images;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 卖家姓名
     */
    private String sellerName;
    
    /**
     * 联系方式
     */
    private String contact;
    
    /**
     * 状态（available-可售，sold-已售，removed-已下架）
     */
    private String status;
    
    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer views;
    
    /**
     * 逻辑删除
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
