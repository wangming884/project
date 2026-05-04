package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.PointsHistory;
import com.campus.entity.User;
import com.campus.mapper.PointsHistoryMapper;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 积分服务
 * 
 * @author Campus Platform Team
 */
@Service
public class PointsService {
    
    private final UserMapper userMapper;
    private final PointsHistoryMapper pointsHistoryMapper;
    
    public PointsService(UserMapper userMapper, PointsHistoryMapper pointsHistoryMapper) {
        this.userMapper = userMapper;
        this.pointsHistoryMapper = pointsHistoryMapper;
    }
    
    /**
     * 获取积分余额
     */
    public Map<String, Object> getBalance(Long userId) {
        User user = userMapper.selectById(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("balance", user.getPoints());
        result.put("lastSignInDate", user.getLastSignInDate());
        
        return result;
    }
    
    /**
     * 每日签到
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> dailySignIn(Long userId) {
        User user = userMapper.selectById(userId);
        String today = LocalDate.now().toString();
        
        // 检查今天是否已签到
        if (today.equals(user.getLastSignInDate())) {
            throw new RuntimeException("今日已签到");
        }
        
        // 计算连续签到天数
        String yesterday = LocalDate.now().minusDays(1).toString();
        int continuousDays = yesterday.equals(user.getLastSignInDate()) 
            ? user.getContinuousDays() + 1 
            : 1;
        
        // 计算奖励积分（连续签到有额外奖励）
        int earnedPoints = 1;
        if (continuousDays >= 7) {
            earnedPoints = 3; // 连续7天奖励3积分
        } else if (continuousDays >= 3) {
            earnedPoints = 2; // 连续3天奖励2积分
        }
        
        // 更新用户积分
        user.setPoints(user.getPoints() + earnedPoints);
        user.setLastSignInDate(today);
        user.setContinuousDays(continuousDays);
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setType("sign_in");
        history.setAmount(earnedPoints);
        history.setBalance(user.getPoints());
        history.setDescription("每日签到");
        pointsHistoryMapper.insert(history);
        
        Map<String, Object> result = new HashMap<>();
        result.put("earnedPoints", earnedPoints);
        result.put("balance", user.getPoints());
        result.put("continuousDays", continuousDays);
        
        return result;
    }
    
    /**
     * 兑换积分码
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> redeem(Long userId, String code) {
        // 简化处理：任意兑换码都给50积分
        // 实际应该验证兑换码的有效性和是否已使用
        int points = 50;
        
        User user = userMapper.selectById(userId);
        user.setPoints(user.getPoints() + points);
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setType("redeem");
        history.setAmount(points);
        history.setBalance(user.getPoints());
        history.setDescription("兑换码: " + code);
        pointsHistoryMapper.insert(history);
        
        Map<String, Object> result = new HashMap<>();
        result.put("points", points);
        result.put("balance", user.getPoints());
        
        return result;
    }

    /**
     * 积分历史记录
     */
    public Page<PointsHistory> getHistory(Long userId, int page, int pageSize) {
        Page<PointsHistory> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<PointsHistory> query = new LambdaQueryWrapper<>();
        query.eq(PointsHistory::getUserId, userId)
             .orderByDesc(PointsHistory::getCreatedAt);
        
        return pointsHistoryMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 增加积分（内部方法）
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, int amount, String description) {
        User user = userMapper.selectById(userId);
        
        user.setPoints(user.getPoints() + amount);
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setType("reward");
        history.setAmount(amount);
        history.setBalance(user.getPoints());
        history.setDescription(description);
        pointsHistoryMapper.insert(history);
    }
    
    /**
     * 扣除积分（内部方法）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductPoints(Long userId, int amount, String description) {
        User user = userMapper.selectById(userId);
        
        if (user.getPoints() < amount) {
            throw new RuntimeException("积分不足");
        }
        
        user.setPoints(user.getPoints() - amount);
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setType("deduct");
        history.setAmount(-amount);
        history.setBalance(user.getPoints());
        history.setDescription(description);
        pointsHistoryMapper.insert(history);
    }
}
