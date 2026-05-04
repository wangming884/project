package com.campus.dto;

import com.campus.common.constant.ApiConstants;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页请求 DTO
 * 
 * @author Campus Platform Team
 */
@Data
public class PageRequest {
    
    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = ApiConstants.DEFAULT_PAGE_NUM;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = ApiConstants.MAX_PAGE_SIZE, message = "每页大小不能超过" + ApiConstants.MAX_PAGE_SIZE)
    private Integer pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
    
    /**
     * 排序字段
     */
    private String sortField;
    
    /**
     * 排序方向（asc/desc）
     */
    private String sortOrder = "desc";
}
