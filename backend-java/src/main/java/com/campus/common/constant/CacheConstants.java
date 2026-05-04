package com.campus.common.constant;

/**
 * 缓存常量
 * 
 * @author Campus Platform Team
 */
public class CacheConstants {
    
    /**
     * 用户信息缓存前缀
     */
    public static final String USER_CACHE_PREFIX = "user:info:";
    
    /**
     * Token 缓存前缀
     */
    public static final String TOKEN_CACHE_PREFIX = "user:token:";
    
    /**
     * 签到记录缓存前缀
     */
    public static final String CHECKIN_CACHE_PREFIX = "checkin:";
    
    /**
     * 二手商品缓存前缀
     */
    public static final String PRODUCT_CACHE_PREFIX = "product:";
    
    /**
     * 代课任务缓存前缀
     */
    public static final String TASK_CACHE_PREFIX = "task:";
    
    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_CACHE_PREFIX = "captcha:";
    
    /**
     * 限流缓存前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    /**
     * 用户信息缓存过期时间（秒）- 30分钟
     */
    public static final long USER_CACHE_EXPIRE = 1800L;
    
    /**
     * Token 缓存过期时间（秒）- 7天
     */
    public static final long TOKEN_CACHE_EXPIRE = 604800L;
    
    /**
     * 验证码过期时间（秒）- 5分钟
     */
    public static final long CAPTCHA_EXPIRE = 300L;
    
    /**
     * 限流时间窗口（秒）- 1分钟
     */
    public static final long RATE_LIMIT_WINDOW = 60L;
    
    private CacheConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
