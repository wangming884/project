package com.campus.service;

import com.campus.entity.AnnouncementMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公告服务（当前为内存存储）
 */
@Service
public class AnnouncementService {

    private final Map<String, AnnouncementMessage> messageStore = new ConcurrentHashMap<>();

    public AnnouncementService() {
        seedDefault();
    }

    private void assertAdmin(Long operatorUserId) {
        if (operatorUserId == null || operatorUserId != 0L) {
            throw new RuntimeException("无管理员权限");
        }
    }

    public Map<String, Object> latest(String scope) {
        String normalized = normalizeScope(scope);
        AnnouncementMessage target = messageStore.get(normalized);
        if (target == null && !"all".equals(normalized)) {
            target = messageStore.get("all");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("scope", normalized);
        data.put("announcement", target);
        return data;
    }

    public Map<String, Object> adminList(Long operatorUserId) {
        assertAdmin(operatorUserId);
        Map<String, Object> data = new HashMap<>();
        data.put("all", messageStore.get("all"));
        data.put("recommend", messageStore.get("recommend"));
        data.put("substitute", messageStore.get("substitute"));
        return data;
    }

    public Map<String, Object> publish(Long operatorUserId, String scope, String title, String content) {
        assertAdmin(operatorUserId);
        if (title == null || title.isBlank()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("公告内容不能为空");
        }

        String normalized = normalizeScope(scope);
        AnnouncementMessage message = new AnnouncementMessage();
        message.setScope(normalized);
        message.setTitle(title.trim());
        message.setContent(content.trim());
        message.setPublishedBy(operatorUserId);
        message.setPublishedAt(LocalDateTime.now());
        messageStore.put(normalized, message);

        Map<String, Object> data = new HashMap<>();
        data.put("announcement", message);
        return data;
    }

    private String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return "all";
        }
        String normalized = scope.trim().toLowerCase();
        if (!"all".equals(normalized) && !"recommend".equals(normalized) && !"substitute".equals(normalized)) {
            throw new RuntimeException("公告范围仅支持 all / recommend / substitute");
        }
        return normalized;
    }

    private void seedDefault() {
        AnnouncementMessage message = new AnnouncementMessage();
        message.setScope("all");
        message.setTitle("平台公告");
        message.setContent("欢迎使用校园综合服务平台，管理员可在后台发布最新通知。");
        message.setPublishedBy(0L);
        message.setPublishedAt(LocalDateTime.now());
        messageStore.put("all", message);
    }
}
