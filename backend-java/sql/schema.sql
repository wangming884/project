-- 校园综合服务平台数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    avatar VARCHAR(500) COMMENT '头像URL',
    points INT DEFAULT 0 COMMENT '积分余额',
    last_sign_in_date VARCHAR(20) COMMENT '最后签到日期',
    continuous_days INT DEFAULT 0 COMMENT '连续签到天数',
    status TINYINT DEFAULT 1 COMMENT '账号状态（0-禁用，1-正常）',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 积分历史表
CREATE TABLE IF NOT EXISTS points_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(20) NOT NULL COMMENT '类型（sign_in-签到，checkin-晚寝签到，redeem-兑换，purchase-购买）',
    amount INT NOT NULL COMMENT '积分变动数量（正数为增加，负数为减少）',
    balance INT NOT NULL COMMENT '变动后余额',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分历史表';

-- 晚寝签到记录表
CREATE TABLE IF NOT EXISTS checkin_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    location VARCHAR(200) NOT NULL COMMENT '签到位置',
    checkin_time DATETIME NOT NULL COMMENT '签到时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending-待审核，approved-已通过，rejected-已拒绝）',
    remark VARCHAR(500) COMMENT '备注',
    review_remark VARCHAR(500) COMMENT '审核备注',
    latitude DECIMAL(10, 8) COMMENT '纬度（可选）',
    longitude DECIMAL(11, 8) COMMENT '经度（可选）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_checkin_time (checkin_time),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='晚寝签到记录表';

-- 二手商品表
CREATE TABLE IF NOT EXISTS secondhand_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    category VARCHAR(50) COMMENT '分类（books-书籍，electronics-电子，daily-日用，transport-交通）',
    description TEXT COMMENT '描述',
    images TEXT COMMENT '图片URL（多张用逗号分隔）',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    seller_name VARCHAR(50) COMMENT '卖家姓名',
    contact VARCHAR(100) COMMENT '联系方式',
    status VARCHAR(20) DEFAULT 'available' COMMENT '状态（available-可售，sold-已售，removed-已下架）',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_seller_id (seller_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手商品表';

-- 代课任务表
CREATE TABLE IF NOT EXISTS substitute_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    course VARCHAR(100) NOT NULL COMMENT '课程名称',
    time DATETIME NOT NULL COMMENT '上课时间',
    location VARCHAR(100) NOT NULL COMMENT '上课地点',
    reward VARCHAR(100) COMMENT '酬金',
    description TEXT COMMENT '任务描述',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_name VARCHAR(50) COMMENT '发布者姓名',
    accepter_id BIGINT COMMENT '接单者ID',
    accepter_name VARCHAR(50) COMMENT '接单者姓名',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending-待接单，accepted-已接单，completed-已完成，cancelled-已取消）',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_accepter_id (accepter_id),
    INDEX idx_status (status),
    INDEX idx_time (time),
    FOREIGN KEY (publisher_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代课任务表';

-- 插入测试数据
-- 密码: 123456 (BCrypt加密后)
INSERT INTO users (username, email, password, points, status) VALUES 
('demo', 'demo@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 100, 1);

-- 插入示例二手商品
INSERT INTO secondhand_products (title, price, category, description, images, seller_id, seller_name, contact, status, view_count) VALUES
('高等数学（第七版）上下册 几乎全新', 25.00, 'books', '几乎全新，无笔记', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=400', 1, '李同学', '微信: demo123', 'available', 120),
('九成新 索尼降噪耳机 毕业出清', 450.00, 'electronics', '九成新，音质完美', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&q=80&w=400', 1, '王学长', '微信: demo123', 'available', 85),
('校园代步自行车 送车锁打气筒', 120.00, 'transport', '八成新，骑行流畅', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=400', 1, '赵同学', '微信: demo123', 'available', 56),
('民谣吉他 适合新手入门 音质好', 180.00, 'daily', '适合新手，音质不错', 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?auto=format&fit=crop&q=80&w=400', 1, '陈同学', '微信: demo123', 'available', 42);
