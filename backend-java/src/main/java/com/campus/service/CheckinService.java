package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.CheckinRecord;
import com.campus.entity.User;
import com.campus.mapper.CheckinRecordMapper;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 晚寝签到服务
 * 
 * @author Campus Platform Team
 */
@Service
public class CheckinService {
    
    private final CheckinRecordMapper checkinRecordMapper;
    private final UserMapper userMapper;
    private final PointsService pointsService;
    
    public CheckinService(CheckinRecordMapper checkinRecordMapper, 
                         UserMapper userMapper,
                         PointsService pointsService) {
        this.checkinRecordMapper = checkinRecordMapper;
        this.userMapper = userMapper;
        this.pointsService = pointsService;
    }
    
    /**
     * 提交晚寝签到
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitCheckin(Long userId, String location, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getPoints() == null || user.getPoints() < 10) {
            throw new RuntimeException("积分不足，晚寝签到需要 10 积分");
        }
        
        String today = LocalDate.now().toString();
        
        // 检查今天是否已签到
        LambdaQueryWrapper<CheckinRecord> query = new LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getUserId, userId)
             .apply("DATE(checkin_time) = {0}", today);
        
        if (checkinRecordMapper.selectCount(query) > 0) {
            throw new RuntimeException("今日已签到");
        }
        
        // 创建签到记录
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setUsername(user.getUsername());
        record.setLocation(location);
        record.setCheckinTime(LocalDateTime.now());
        record.setStatus("pending");
        record.setRemark(remark);

        checkinRecordMapper.insert(record);

        // 晚寝签到提交后先扣除积分
        pointsService.deductPoints(userId, 10, "晚寝签到扣除");

        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("checkinTime", record.getCheckinTime());
        result.put("status", record.getStatus());
        result.put("remainingPoints", user.getPoints() - 10);

        return result;
    }
    
    /**
     * 获取签到记录列表
     */
    public Page<CheckinRecord> getCheckinRecords(Long userId, int page, int pageSize) {
        Page<CheckinRecord> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<CheckinRecord> query = new LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getUserId, userId)
             .orderByDesc(CheckinRecord::getCheckinTime);
        
        return checkinRecordMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取今日签到状态
     */
    public Map<String, Object> getTodayStatus(Long userId) {
        String today = LocalDate.now().toString();
        
        LambdaQueryWrapper<CheckinRecord> query = new LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getUserId, userId)
             .apply("DATE(checkin_time) = {0}", today);
        
        CheckinRecord record = checkinRecordMapper.selectOne(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("hasCheckedIn", record != null);
        result.put("record", record);
        
        return result;
    }
    
    /**
     * 审核签到记录（管理员功能）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> approveCheckin(Long recordId, String status, String reviewRemark) {
        CheckinRecord record = checkinRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("签到记录不存在");
        }
        
        if (!"pending".equals(record.getStatus())) {
            throw new RuntimeException("该记录已审核");
        }
        
        // 更新记录状态
        record.setStatus(status);
        record.setReviewRemark(reviewRemark);
        checkinRecordMapper.updateById(record);
        
        // 如果审核通过，奖励积分
        if ("approved".equals(status)) {
            pointsService.addPoints(record.getUserId(), 5, "晚寝签到奖励");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", recordId);
        result.put("status", status);
        
        return result;
    }
    
    /**
     * 获取所有待审核的签到记录（管理员功能）
     */
    public Page<CheckinRecord> getPendingRecords(int page, int pageSize) {
        Page<CheckinRecord> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<CheckinRecord> query = new LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getStatus, "pending")
             .orderByAsc(CheckinRecord::getCheckinTime);
        
        return checkinRecordMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取签到统计
     */
    public Map<String, Object> getStatistics(Long userId) {
        LambdaQueryWrapper<CheckinRecord> query = new LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getUserId, userId);
        
        Long totalCount = checkinRecordMapper.selectCount(query);
        
        query.eq(CheckinRecord::getStatus, "approved");
        Long approvedCount = checkinRecordMapper.selectCount(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("approvedCount", approvedCount);
        result.put("approvalRate", totalCount > 0 ? (approvedCount * 100.0 / totalCount) : 0);
        
        return result;
    }
}
