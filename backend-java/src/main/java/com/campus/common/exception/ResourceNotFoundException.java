package com.campus.common.exception;

/**
 * 资源未找到异常
 * 
 * @author Campus Platform Team
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    public ResourceNotFoundException(String resource, Object id) {
        super(404, String.format("%s not found with id: %s", resource, id));
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}
