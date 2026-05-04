package com.campus.common.constant;

/**
 * 错误码常量
 * 
 * @author Campus Platform Team
 */
public class ErrorCode {
    
    // 通用错误码 (1000-1999)
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int INTERNAL_ERROR = 500;
    
    // 用户相关错误码 (2000-2999)
    public static final int USER_NOT_FOUND = 2001;
    public static final int USER_ALREADY_EXISTS = 2002;
    public static final int INVALID_PASSWORD = 2003;
    public static final int USER_DISABLED = 2004;
    public static final int INVALID_TOKEN = 2005;
    public static final int TOKEN_EXPIRED = 2006;
    
    // 积分相关错误码 (3000-3999)
    public static final int INSUFFICIENT_POINTS = 3001;
    public static final int ALREADY_SIGNED_IN = 3002;
    public static final int INVALID_REDEEM_CODE = 3003;
    
    // 签到相关错误码 (4000-4999)
    public static final int ALREADY_CHECKED_IN = 4001;
    public static final int CHECKIN_NOT_FOUND = 4002;
    public static final int INVALID_CHECKIN_STATUS = 4003;
    
    // 二手交易相关错误码 (5000-5999)
    public static final int PRODUCT_NOT_FOUND = 5001;
    public static final int PRODUCT_ALREADY_SOLD = 5002;
    public static final int NOT_PRODUCT_OWNER = 5003;
    
    // 代课平台相关错误码 (6000-6999)
    public static final int TASK_NOT_FOUND = 6001;
    public static final int TASK_ALREADY_ACCEPTED = 6002;
    public static final int CANNOT_ACCEPT_OWN_TASK = 6003;
    public static final int NOT_TASK_PUBLISHER = 6004;
    public static final int NOT_TASK_ACCEPTER = 6005;
    public static final int INVALID_TASK_STATUS = 6006;
    
    private ErrorCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
