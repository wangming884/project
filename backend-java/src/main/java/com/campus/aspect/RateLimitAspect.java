package com.campus.aspect;

import com.campus.annotation.RateLimit;
import com.campus.common.exception.BusinessException;
import com.campus.util.IpUtil;
import com.campus.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 
 * @author Campus Platform Team
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {
    
    private final RedisUtil redisUtil;
    
    public RateLimitAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
    
    @Before("@annotation(com.campus.annotation.RateLimit)")
    public void doBefore(JoinPoint joinPoint) {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        // 获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        
        // 构建限流key
        String key = buildKey(rateLimit, request);
        
        // 获取当前请求次数
        Long count = redisUtil.increment(key);
        
        // 第一次请求，设置过期时间
        if (count != null && count == 1) {
            redisUtil.expire(key, rateLimit.time(), TimeUnit.SECONDS);
        }
        
        // 判断是否超过限流次数
        if (count != null && count > rateLimit.count()) {
            log.warn("Rate limit exceeded - Key: {}, Count: {}, Limit: {}", key, count, rateLimit.count());
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
        
        log.debug("Rate limit check - Key: {}, Count: {}/{}", key, count, rateLimit.count());
    }
    
    /**
     * 构建限流key
     */
    private String buildKey(RateLimit rateLimit, HttpServletRequest request) {
        StringBuilder key = new StringBuilder(rateLimit.key());
        key.append(":");
        
        // 根据限流类型构建key
        if (rateLimit.limitType() == RateLimit.LimitType.IP) {
            key.append(IpUtil.getClientIp(request));
        } else {
            // 从请求头获取用户ID
            String userId = request.getHeader("X-User-Id");
            key.append(userId != null ? userId : "anonymous");
        }
        
        // 添加方法标识
        key.append(":").append(request.getRequestURI());
        
        return key.toString();
    }
}
