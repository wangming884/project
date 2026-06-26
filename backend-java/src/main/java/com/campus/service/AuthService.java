package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 * 
 * @author Campus Platform Team
 */
@Service
public class AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:Admin@123456}")
    private String adminPassword;
    
    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 用户注册
     */
    public Map<String, Object> register(String username, String email, String password) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(User::getUsername, username);
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, email);
        if (userMapper.selectCount(emailQuery) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPoints(0);
        user.setContinuousDays(0);
        user.setStatus(1);
        
        userMapper.insert(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        
        return result;
    }
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        if (adminUsername.equals(username)) {
            return loginAsAdmin(username, password);
        }

        // 查找用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username)
             .or()
             .eq(User::getEmail, username);
        
        User user = userMapper.selectOne(query);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成 Token
        String token = jwtUtil.generateToken(user.getId());
        
        // 构建返回数据
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("avatar", user.getAvatar());
        userData.put("points", user.getPoints());
        userData.put("role", "user");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userData);
        
        return result;
    }

    /**
     * 管理员登录
     */
    private Map<String, Object> loginAsAdmin(String username, String password) {
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            throw new RuntimeException("管理员账号或密码错误");
        }

        return buildAdminLoginResult();
    }

    private Map<String, Object> buildAdminLoginResult() {
        // 管理员使用保留ID=0，便于在业务层识别超级权限
        String token = jwtUtil.generateToken(0L);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", 0);
        userData.put("username", adminUsername);
        userData.put("role", "admin");
        userData.put("permissions", "all");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userData);

        return result;
    }
    
    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("points", user.getPoints());
        result.put("createdAt", user.getCreatedAt());
        
        return result;
    }
}
