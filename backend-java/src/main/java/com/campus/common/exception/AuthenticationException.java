package com.campus.common.exception;

/**
 * 认证异常
 * 
 * @author Campus Platform Team
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(401, message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(401, message, cause);
    }
}
