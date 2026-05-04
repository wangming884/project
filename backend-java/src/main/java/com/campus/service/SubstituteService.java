package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.SubstituteTask;
import com.campus.entity.User;
import com.campus.mapper.SubstituteTaskMapper;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 代课平台服务
 * 
 * @author Campus Platform Team
 */
@Service
public class SubstituteService {
    
    private final SubstituteTaskMapper taskMapper;
    private final UserMapper userMapper;
    
    public SubstituteService(SubstituteTaskMapper taskMapper, UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
    }
    
    /**
     * 发布代课任务
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> publishTask(Long userId, String title, String course, 
                                          LocalDateTime time, String location, 
                                          String reward, String description) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建任务
        SubstituteTask task = new SubstituteTask();
        task.setPublisherId(userId);
        task.setPublisherName(user.getUsername());
        task.setTitle(title);
        task.setCourse(course);
        task.setTime(time);
        task.setLocation(location);
        task.setReward(reward);
        task.setDescription(description);
        task.setStatus("pending");
        
        taskMapper.insert(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", task.getId());
        result.put("title", task.getTitle());
        result.put("status", task.getStatus());
        
        return result;
    }
    
    /**
     * 获取任务列表
     */
    public Page<SubstituteTask> getTasks(String status, String keyword, 
                                        String sortBy, int page, int pageSize) {
        Page<SubstituteTask> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SubstituteTask> query = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (status != null && !status.isEmpty()) {
            query.eq(SubstituteTask::getStatus, status);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            query.and(wrapper -> wrapper
                .like(SubstituteTask::getTitle, keyword)
                .or()
                .like(SubstituteTask::getCourse, keyword)
                .or()
                .like(SubstituteTask::getDescription, keyword)
            );
        }
        
        // 排序
        if ("time_asc".equals(sortBy)) {
            query.orderByAsc(SubstituteTask::getTime);
        } else if ("time_desc".equals(sortBy)) {
            query.orderByDesc(SubstituteTask::getTime);
        } else {
            query.orderByDesc(SubstituteTask::getCreatedAt);
        }
        
        return taskMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取任务详情
     */
    public Map<String, Object> getTaskDetail(Long taskId) {
        SubstituteTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("task", task);
        
        return result;
    }
    
    /**
     * 接单
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> acceptTask(Long taskId, Long userId) {
        SubstituteTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!"pending".equals(task.getStatus())) {
            throw new RuntimeException("任务已被接单或已完成");
        }
        
        if (task.getPublisherId().equals(userId)) {
            throw new RuntimeException("不能接自己发布的任务");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新任务状态
        task.setAccepterId(userId);
        task.setAccepterName(user.getUsername());
        task.setStatus("accepted");
        taskMapper.updateById(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", "accepted");
        result.put("accepterName", user.getUsername());
        
        return result;
    }
    
    /**
     * 取消接单
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelAccept(Long taskId, Long userId) {
        SubstituteTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!task.getAccepterId().equals(userId)) {
            throw new RuntimeException("无权取消此任务");
        }
        
        if (!"accepted".equals(task.getStatus())) {
            throw new RuntimeException("任务状态不允许取消");
        }
        
        // 恢复任务状态
        task.setAccepterId(null);
        task.setAccepterName(null);
        task.setStatus("pending");
        taskMapper.updateById(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", "pending");
        
        return result;
    }
    
    /**
     * 完成任务
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> completeTask(Long taskId, Long userId) {
        SubstituteTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!task.getPublisherId().equals(userId)) {
            throw new RuntimeException("只有发布者可以确认完成");
        }
        
        if (!"accepted".equals(task.getStatus())) {
            throw new RuntimeException("任务状态不正确");
        }
        
        // 更新任务状态
        task.setStatus("completed");
        taskMapper.updateById(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", "completed");
        
        return result;
    }
    
    /**
     * 取消任务
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelTask(Long taskId, Long userId) {
        SubstituteTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!task.getPublisherId().equals(userId)) {
            throw new RuntimeException("只有发布者可以取消任务");
        }
        
        if ("completed".equals(task.getStatus())) {
            throw new RuntimeException("已完成的任务不能取消");
        }
        
        // 更新任务状态
        task.setStatus("cancelled");
        taskMapper.updateById(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("status", "cancelled");
        
        return result;
    }
    
    /**
     * 获取我发布的任务
     */
    public Page<SubstituteTask> getMyPublishedTasks(Long userId, int page, int pageSize) {
        Page<SubstituteTask> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SubstituteTask> query = new LambdaQueryWrapper<>();
        query.eq(SubstituteTask::getPublisherId, userId)
             .orderByDesc(SubstituteTask::getCreatedAt);
        
        return taskMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取我接的任务
     */
    public Page<SubstituteTask> getMyAcceptedTasks(Long userId, int page, int pageSize) {
        Page<SubstituteTask> pageInfo = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<SubstituteTask> query = new LambdaQueryWrapper<>();
        query.eq(SubstituteTask::getAccepterId, userId)
             .orderByDesc(SubstituteTask::getCreatedAt);
        
        return taskMapper.selectPage(pageInfo, query);
    }
    
    /**
     * 获取任务统计
     */
    public Map<String, Object> getStatistics(Long userId) {
        // 发布的任务统计
        LambdaQueryWrapper<SubstituteTask> publishQuery = new LambdaQueryWrapper<>();
        publishQuery.eq(SubstituteTask::getPublisherId, userId);
        Long publishedCount = taskMapper.selectCount(publishQuery);
        
        publishQuery.eq(SubstituteTask::getStatus, "completed");
        Long publishedCompletedCount = taskMapper.selectCount(publishQuery);
        
        // 接的任务统计
        LambdaQueryWrapper<SubstituteTask> acceptQuery = new LambdaQueryWrapper<>();
        acceptQuery.eq(SubstituteTask::getAccepterId, userId);
        Long acceptedCount = taskMapper.selectCount(acceptQuery);
        
        acceptQuery.eq(SubstituteTask::getStatus, "completed");
        Long acceptedCompletedCount = taskMapper.selectCount(acceptQuery);
        
        Map<String, Object> result = new HashMap<>();
        result.put("publishedCount", publishedCount);
        result.put("publishedCompletedCount", publishedCompletedCount);
        result.put("acceptedCount", acceptedCount);
        result.put("acceptedCompletedCount", acceptedCompletedCount);
        
        return result;
    }
}
