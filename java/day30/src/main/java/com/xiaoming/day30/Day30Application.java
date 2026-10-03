package com.xiaoming.day30;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 30：SpringBoot 启动类 —— 整个程序的"总开关"
 *
 * ⭐ 这个类和 Day29 的 HelloMaven 最大的区别：
 *   Day29：你自己写 main，自己写一堆 System.out.println 验证
 *   Day30：main 里**只有一行** —— 把控制权交给 SpringBoot。
 *          它启动内嵌 Tomcat、扫描你写的 @RestController、准备好数据库……
 *
 * 就这一行 `SpringApplication.run(...)`，干掉了以前要写几十行的"搭环境"：
 *   · 启动 Tomcat（不用装、不用配、不用部署 war）
 *   · 建立数据库连接池（不用 DriverManager.getConnection）
 *   · 扫描并注册所有 @RestController / @Service / @Repository
 *   · 读 application.properties
 *
 * ⚠️ @SpringBootApplication 的位置很重要：
 *   它会从**这个类所在的包（com.xiaoming.day30）往下扫描**所有子包。
 *   所以你写的 Controller 必须放在 com.xiaoming.day30 或它的子包里，
 *   放到外面 SpringBoot 就找不到 —— 这是新手第一个经典坑。
 *
 * 怎么跑（在 java/day30 目录下）：
 *   运行SpringBoot.bat 选 1
 *   或 mvn spring-boot:run
 *
 * 期望输出（已真机验证）：
 *   ==========================================
 *     .   ____          _            __ _ _
 *    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
 *   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 *    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
 *     '  |____| .__|_| |_|_| |_\__, | / / / /
 *   =========|_|==============|___/=/_/_/_/
 *    :: Spring Boot ::                (v3.3.5)
 *   ...
 *   Tomcat started on port 8080 (http)
 *   Started Day30Application in 1.8 seconds
 */
@SpringBootApplication
public class Day30Application {

    public static void main(String[] args) {
        SpringApplication.run(Day30Application.class, args);
    }
}