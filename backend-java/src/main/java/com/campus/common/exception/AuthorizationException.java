package com.campus.common.exception;

/**
 * 授权异常
 * 
 * @author Campus Platform Team
 */
public class AuthorizationException extends BusinessException {
    
    public AuthorizationException(String message) {
        super(403, message);
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super(403, message, cause);
    }
}
