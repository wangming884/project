package com.campus.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 * 
 * @author Campus Platform Team
 */
public class IpUtil {
    
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    
    /**
     * 获取客户端真实IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况（取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        // 处理本地IP
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }
        
        return ip;
    }
    
    /**
     * 判断IP是否无效
     */
    private static boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip);
    }
}
