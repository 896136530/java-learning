package com.xiaoming.day34;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 34：Spring 事务管理（@Transactional）—— 启动类
 *
 * ⭐ 和 Day30~33 的启动类长得一样（只改了名字），但今天多了两件"看不见的自动装配"：
 *
 *   ① **DataSource**：因为 pom 里有 spring-boot-starter-jdbc + h2，
 *      SpringBoot 看到 application.properties 里的 spring.datasource.url
 *      就自动建好了一个 HikariCP 连接池 + 一个 `JdbcTemplate` Bean（直接注入就能用）。
 *
 *   ② **TransactionManager（事务管理器）**：有 DataSourceAutoConfiguration 就会顺带建一个
 *      `DataSourceTransactionManager` —— ⭐ 它才是真正执行 `commit()` / `rollback()` 的人。
 *
 *   ⭐ 下面这行注解**故意注释掉**了：
 *
 *      @EnableTransactionManagement
 *
 *      它的作用是"开启 @Transactional 注解支持"。但 SpringBoot 的自动装配
 *      （TransactionAutoConfiguration）已经替你加了，所以**写了也能跑、不写也能跑**。
 *      放在这里是为了让你第一次"亲眼看到有这个东西的存在"——
 *      哪天你在纯 Spring（没有 SpringBoot）的项目里，就必须自己写上它了。
 *
 * @SpringBootApplication 的位置还是老规矩：从 com.xiaoming.day34 往下扫描所有子包
 * （今天多出来的包：db、order）
 */
// @EnableTransactionManagement        // ← 想验证"是不是真的自动开了"？取消注释，行为一模一样
@SpringBootApplication
public class Day34Application {

    public static void main(String[] args) {
        SpringApplication.run(Day34Application.class, args);
    }
}