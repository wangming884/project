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

    private void assertAdmin(Long operatorUserId) {
        if (operatorUserId == null || operatorUserId != 0L) {
            throw new RuntimeException("无管理员权限");
        }
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
     * 购买积分（当前为模拟下单，后续可接入真实支付）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchase(Long userId, String packageType) {
        String type = packageType == null ? "" : packageType.trim().toLowerCase();
        int points;
        int amountYuan;
        if ("basic".equals(type)) {
            points = 100;
            amountYuan = 10;
        } else if ("plus".equals(type)) {
            points = 300;
            amountYuan = 25;
        } else if ("pro".equals(type)) {
            points = 800;
            amountYuan = 60;
        } else {
            throw new RuntimeException("积分套餐不存在，仅支持 basic / plus / pro");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int current = user.getPoints() == null ? 0 : user.getPoints();
        int next = current + points;
        user.setPoints(next);
        userMapper.updateById(user);

        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setType("purchase");
        history.setAmount(points);
        history.setBalance(next);
        history.setDescription("购买积分套餐(" + type + ")，支付￥" + amountYuan);
        pointsHistoryMapper.insert(history);

        Map<String, Object> result = new HashMap<>();
        result.put("packageType", type);
        result.put("paidAmount", amountYuan);
        result.put("points", points);
        result.put("balance", next);
        result.put("orderNo", "PO-" + System.currentTimeMillis());
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
     * 管理员分页查询用户列表
     */
    public Page<User> listUsersForAdmin(String keyword, int page, int pageSize) {
        Page<User> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getDeleted, 0);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            query.and(wrapper -> wrapper
                .like(User::getUsername, k)
                .or()
                .like(User::getEmail, k)
            );
        }

        query.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(pageInfo, query);
    }

    /**
     * 管理员调整用户积分（正数增加，负数扣除）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminAdjustPoints(Long operatorUserId, Long targetUserId, int delta, String reason) {
        assertAdmin(operatorUserId);
        if (targetUserId == null || targetUserId <= 0) {
            throw new RuntimeException("目标用户ID不合法");
        }
        if (delta == 0) {
            throw new RuntimeException("积分调整值不能为 0");
        }

        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new RuntimeException("目标用户不存在");
        }

        int currentPoints = target.getPoints() == null ? 0 : target.getPoints();
        int nextPoints = currentPoints + delta;
        if (nextPoints < 0) {
            throw new RuntimeException("扣除失败，用户当前积分不足");
        }

        target.setPoints(nextPoints);
        userMapper.updateById(target);

        PointsHistory history = new PointsHistory();
        history.setUserId(targetUserId);
        history.setType("admin_adjust");
        history.setAmount(delta);
        history.setBalance(nextPoints);
        history.setDescription((reason == null || reason.isBlank() ? "管理员积分调整" : reason) + "（管理员操作）");
        pointsHistoryMapper.insert(history);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", targetUserId);
        result.put("username", target.getUsername());
        result.put("delta", delta);
        result.put("balance", nextPoints);
        result.put("reason", reason);
        return result;
    }

    /**
     * 管理员修改用户账号状态
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminUpdateUserStatus(Long operatorUserId, Long targetUserId, Integer status, String reason) {
        assertAdmin(operatorUserId);
        if (targetUserId == null || targetUserId <= 0) {
            throw new RuntimeException("目标用户ID不合法");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("状态参数不合法，仅支持 0(禁用) / 1(启用)");
        }

        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new RuntimeException("目标用户不存在");
        }

        target.setStatus(status);
        userMapper.updateById(target);

        PointsHistory history = new PointsHistory();
        history.setUserId(targetUserId);
        history.setType("admin_user_status");
        history.setAmount(0);
        history.setBalance(target.getPoints() == null ? 0 : target.getPoints());
        history.setDescription((status == 1 ? "管理员启用账号" : "管理员禁用账号")
            + ((reason == null || reason.isBlank()) ? "" : ("，原因：" + reason)));
        pointsHistoryMapper.insert(history);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", targetUserId);
        result.put("username", target.getUsername());
        result.put("status", status);
        result.put("reason", reason);
        return result;
    }

    /**
     * 管理员重置用户签到连续信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminResetUserSignIn(Long operatorUserId, Long targetUserId, String reason) {
        assertAdmin(operatorUserId);
        if (targetUserId == null || targetUserId <= 0) {
            throw new RuntimeException("目标用户ID不合法");
        }

        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new RuntimeException("目标用户不存在");
        }

        target.setLastSignInDate(null);
        target.setContinuousDays(0);
        userMapper.updateById(target);

        PointsHistory history = new PointsHistory();
        history.setUserId(targetUserId);
        history.setType("admin_sign_reset");
        history.setAmount(0);
        history.setBalance(target.getPoints() == null ? 0 : target.getPoints());
        history.setDescription("管理员重置签到记录"
            + ((reason == null || reason.isBlank()) ? "" : ("，原因：" + reason)));
        pointsHistoryMapper.insert(history);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", targetUserId);
        result.put("username", target.getUsername());
        result.put("lastSignInDate", target.getLastSignInDate());
        result.put("continuousDays", target.getContinuousDays());
        return result;
    }

    /**
     * 管理员查看积分流水
     */
    public Page<PointsHistory> adminGetHistory(Long operatorUserId, Long userId, String type, int page, int pageSize) {
        assertAdmin(operatorUserId);

        Page<PointsHistory> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<PointsHistory> query = new LambdaQueryWrapper<>();
        if (userId != null && userId > 0) {
            query.eq(PointsHistory::getUserId, userId);
        }
        if (type != null && !type.isBlank()) {
            query.eq(PointsHistory::getType, type.trim());
        }
        query.orderByDesc(PointsHistory::getCreatedAt);
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
