package com.campus.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问层
 * 
 * @author Campus Platform Team
 */
@Repository
public class UserDao implements BaseDao<User, UserMapper> {
    
    private final UserMapper userMapper;
    
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    @Override
    public UserMapper getMapper() {
        return userMapper;
    }
    
    /**
     * 根据用户名查询
     */
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 根据邮箱查询
     */
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 根据用户名或邮箱查询
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, usernameOrEmail)
               .or()
               .eq(User::getEmail, usernameOrEmail);
        return findOneByCondition(wrapper);
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return exists(wrapper);
    }
    
    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return exists(wrapper);
    }
    
    /**
     * 更新用户积分
     */
    public int updatePoints(Long userId, int points) {
        User user = findById(userId);
        if (user != null) {
            user.setPoints(points);
            return updateById(user);
        }
        return 0;
    }
}
