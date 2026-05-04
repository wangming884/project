package com.campus.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告消息实体（内存实现）
 */
@Data
public class AnnouncementMessage {

    /**
     * 作用域：all / recommend / substitute
     */
    private String scope;

    private String title;

    private String content;

    private Long publishedBy;

    private LocalDateTime publishedAt;
}
