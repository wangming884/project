package com.campus.interceptor;

import cn.hutool.core.util.IdUtil;
import com.campus.common.constant.ApiConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求日志拦截器
 * 记录每个请求的基本信息和执行时间
 * 
 * @author Campus Platform Team
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    
    private static final String START_TIME = "startTime";
    private static final String REQUEST_ID = "requestId";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成请求ID
        String requestId = IdUtil.simpleUUID();
        request.setAttribute(REQUEST_ID, requestId);
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        
        // 添加请求ID到响应头
        response.setHeader(ApiConstants.HEADER_REQUEST_ID, requestId);
        
        // 记录请求信息
        log.info("Request Start - ID: {}, Method: {}, URI: {}, IP: {}", 
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request));
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 获取请求ID和开始时间
        String requestId = (String) request.getAttribute(REQUEST_ID);
        Long startTime = (Long) request.getAttribute(START_TIME);
        
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录请求完成信息
            log.info("Request End - ID: {}, Status: {}, Duration: {}ms", 
                    requestId,
                    response.getStatus(),
                    duration);
            
            // 如果请求时间过长，记录警告
            if (duration > 3000) {
                log.warn("Slow Request - ID: {}, Duration: {}ms, URI: {}", 
                        requestId, duration, request.getRequestURI());
            }
        }
        
        // 如果有异常，记录错误
        if (ex != null) {
            log.error("Request Error - ID: {}, Exception: {}", requestId, ex.getMessage(), ex);
        }
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
