package com.campus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园综合服务平台启动类
 * 
 * @author Campus Platform Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.campus.mapper")
public class CampusPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPlatformApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🚀 校园综合服务平台启动成功！");
        System.out.println("📍 接口地址: http://localhost:8080/api");
        System.out.println("📚 接口文档: http://localhost:8080/api/doc.html");
        System.out.println("========================================\n");
    }
}
