package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.CourseBrushCourse;
import com.campus.entity.CourseBrushOrder;
import com.campus.entity.User;
import com.campus.mapper.CourseBrushCourseMapper;
import com.campus.mapper.CourseBrushOrderMapper;
import com.campus.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 代刷课服务
 */
@Service
public class CourseBrushService {

    private final CourseBrushCourseMapper courseMapper;
    private final CourseBrushOrderMapper orderMapper;
    private final UserMapper userMapper;
    private final PointsService pointsService;

    public CourseBrushService(CourseBrushCourseMapper courseMapper,
                              CourseBrushOrderMapper orderMapper,
                              UserMapper userMapper,
                              PointsService pointsService) {
        this.courseMapper = courseMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.pointsService = pointsService;
    }

    private boolean isAdmin(Long userId) {
        return userId != null && userId == 0L;
    }

    private CourseBrushCourse requireCourse(Long courseId) {
        CourseBrushCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        return course;
    }

    private CourseBrushOrder requireOrder(Long orderId) {
        CourseBrushOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }

    public Page<CourseBrushCourse> getCourses(Boolean onlyEnabled, String keyword, int page, int pageSize) {
        Page<CourseBrushCourse> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<CourseBrushCourse> query = new LambdaQueryWrapper<>();
        if (Boolean.TRUE.equals(onlyEnabled)) {
            query.eq(CourseBrushCourse::getEnabled, 1);
        }
        if (keyword != null && !keyword.isBlank()) {
            String key = keyword.trim();
            query.and(wrapper -> wrapper
                .like(CourseBrushCourse::getCourseName, key)
                .or()
                .like(CourseBrushCourse::getCourseCode, key)
                .or()
                .like(CourseBrushCourse::getDescription, key));
        }
        query.orderByDesc(CourseBrushCourse::getEnabled)
            .orderByDesc(CourseBrushCourse::getUpdatedAt)
            .orderByDesc(CourseBrushCourse::getCreatedAt);
        return courseMapper.selectPage(pageInfo, query);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitOrder(Long userId, Long courseId, String studentAccount, String studentPassword, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        CourseBrushCourse course = requireCourse(courseId);
        if (!Integer.valueOf(1).equals(course.getEnabled())) {
            throw new RuntimeException("该课程当前未开放下单");
        }

        if (studentAccount == null || studentAccount.isBlank()) {
            throw new RuntimeException("请填写刷课账号/学号");
        }
        if (studentPassword == null || studentPassword.isBlank()) {
            throw new RuntimeException("请填写刷课密码");
        }

        int requiredPoints = course.getRequiredPoints() == null ? 0 : course.getRequiredPoints();
        int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
        if (requiredPoints <= 0) {
            throw new RuntimeException("课程积分配置无效，请联系管理员");
        }
        if (currentPoints < requiredPoints) {
            throw new RuntimeException("积分不足，该课程需要 " + requiredPoints + " 积分");
        }

        CourseBrushOrder order = new CourseBrushOrder();
        order.setUserId(userId);
        order.setUsername(user.getUsername());
        order.setCourseId(course.getId());
        order.setCourseName(course.getCourseName());
        order.setRequiredPoints(requiredPoints);
        order.setStudentAccount(studentAccount.trim());
        order.setStudentPassword(studentPassword);
        order.setRemark(remark == null ? "" : remark.trim());
        order.setStatus("pending");
        order.setSource("manual");
        order.setSubmittedAt(LocalDateTime.now());
        orderMapper.insert(order);

        pointsService.deductPoints(userId, requiredPoints, "代刷课下单：" + course.getCourseName());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("courseId", course.getId());
        result.put("courseName", course.getCourseName());
        result.put("requiredPoints", requiredPoints);
        result.put("status", order.getStatus());
        result.put("remainingPoints", currentPoints - requiredPoints);
        result.put("submittedAt", order.getSubmittedAt());
        return result;
    }

    public Page<CourseBrushOrder> getMyOrders(Long userId, String status, int page, int pageSize) {
        Page<CourseBrushOrder> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<CourseBrushOrder> query = new LambdaQueryWrapper<>();
        query.eq(CourseBrushOrder::getUserId, userId);
        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status.trim())) {
            query.eq(CourseBrushOrder::getStatus, status.trim());
        }
        query.orderByDesc(CourseBrushOrder::getSubmittedAt)
            .orderByDesc(CourseBrushOrder::getCreatedAt);
        return orderMapper.selectPage(pageInfo, query);
    }

    public Page<CourseBrushOrder> getAdminOrders(Long operatorUserId, String status, String keyword, int page, int pageSize) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }
        Page<CourseBrushOrder> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<CourseBrushOrder> query = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status.trim())) {
            query.eq(CourseBrushOrder::getStatus, status.trim());
        }
        if (keyword != null && !keyword.isBlank()) {
            String key = keyword.trim();
            query.and(wrapper -> wrapper
                .like(CourseBrushOrder::getUsername, key)
                .or()
                .like(CourseBrushOrder::getCourseName, key)
                .or()
                .like(CourseBrushOrder::getStudentAccount, key)
                .or()
                .like(CourseBrushOrder::getRemark, key)
                .or()
                .like(CourseBrushOrder::getRequestId, key));
        }
        query.orderByDesc(CourseBrushOrder::getSubmittedAt)
            .orderByDesc(CourseBrushOrder::getCreatedAt);
        return orderMapper.selectPage(pageInfo, query);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminSaveCourse(Long operatorUserId, Long courseId, String courseName, String courseCode,
                                               Integer requiredPoints, String description, Integer enabled) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }
        if (courseName == null || courseName.isBlank()) {
            throw new RuntimeException("课程名称不能为空");
        }
        if (requiredPoints == null || requiredPoints <= 0) {
            throw new RuntimeException("课程积分必须为正整数");
        }

        CourseBrushCourse course = courseId == null ? new CourseBrushCourse() : requireCourse(courseId);
        course.setCourseName(courseName.trim());
        course.setCourseCode(courseCode == null ? "" : courseCode.trim());
        course.setRequiredPoints(requiredPoints);
        course.setDescription(description == null ? "" : description.trim());
        course.setEnabled(enabled != null && enabled == 0 ? 0 : 1);

        if (courseId == null) {
            courseMapper.insert(course);
        } else {
            courseMapper.updateById(course);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getId());
        result.put("courseName", course.getCourseName());
        result.put("requiredPoints", course.getRequiredPoints());
        result.put("enabled", course.getEnabled());
        result.put("action", courseId == null ? "created" : "updated");
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminUpdateCourseStatus(Long operatorUserId, Long courseId, Integer enabled) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }
        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new RuntimeException("状态仅支持 1(启用) / 0(停用)");
        }

        CourseBrushCourse course = requireCourse(courseId);
        course.setEnabled(enabled);
        courseMapper.updateById(course);

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", courseId);
        result.put("enabled", enabled);
        result.put("courseName", course.getCourseName());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> adminUpdateOrderStatus(Long operatorUserId, Long orderId, String status, String resultMessage) {
        if (!isAdmin(operatorUserId)) {
            throw new RuntimeException("无管理员权限");
        }
        return updateOrderStatusInternal(orderId, status, resultMessage, "admin-console", null, "manual");
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> automationUpdateOrderStatus(Long orderId, String status, String resultMessage, String scriptName, String requestId) {
        return updateOrderStatusInternal(orderId, status, resultMessage, scriptName, requestId, "automation-script");
    }

    private Map<String, Object> updateOrderStatusInternal(Long orderId, String status, String resultMessage,
                                                          String scriptName, String requestId, String source) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("订单状态不能为空");
        }
        String normalizedStatus = status.trim();
        if (!"pending".equals(normalizedStatus)
            && !"processing".equals(normalizedStatus)
            && !"completed".equals(normalizedStatus)
            && !"failed".equals(normalizedStatus)
            && !"cancelled".equals(normalizedStatus)) {
            throw new RuntimeException("订单状态仅支持 pending / processing / completed / failed / cancelled");
        }

        CourseBrushOrder order = requireOrder(orderId);
        String oldStatus = order.getStatus();
        order.setStatus(normalizedStatus);
        order.setResultMessage(resultMessage == null ? "" : resultMessage.trim());
        order.setScriptName(scriptName == null ? "" : scriptName.trim());
        order.setRequestId(requestId == null ? "" : requestId.trim());
        order.setSource(source);
        if (!"pending".equals(normalizedStatus)) {
            order.setProcessedAt(LocalDateTime.now());
        }
        orderMapper.updateById(order);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("oldStatus", oldStatus);
        result.put("status", normalizedStatus);
        result.put("courseName", order.getCourseName());
        result.put("scriptName", order.getScriptName());
        result.put("requestId", order.getRequestId());
        result.put("source", source);
        return result;
    }
}
