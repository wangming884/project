package com.campus.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 
 * @author Campus Platform Team
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限流key前缀
     */
    String key() default "rate_limit";
    
    /**
     * 时间窗口（秒）
     */
    int time() default 60;
    
    /**
     * 最大请求次数
     */
    int count() default 60;
    
    /**
     * 限流类型（IP/USER）
     */
    LimitType limitType() default LimitType.IP;
    
    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 根据IP限流
         */
        IP,
        
        /**
         * 根据用户限流
         */
        USER
    }
}
