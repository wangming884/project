package com.campus.common.constant;

/**
 * API 常量
 * 
 * @author Campus Platform Team
 */
public class ApiConstants {
    
    /**
     * API 版本
     */
    public static final String API_VERSION = "v1.0.0";
    
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /**
     * 默认每页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 最大每页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 请求头 - Token
     */
    public static final String HEADER_TOKEN = "Authorization";
    
    /**
     * 请求头 - Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 请求头 - 用户ID
     */
    public static final String HEADER_USER_ID = "X-User-Id";
    
    /**
     * 请求头 - 请求ID
     */
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    
    /**
     * 限流 - 每分钟最大请求数
     */
    public static final int RATE_LIMIT_PER_MINUTE = 60;
    
    /**
     * 文件上传 - 最大文件大小（10MB）
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 文件上传 - 允许的图片格式
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
