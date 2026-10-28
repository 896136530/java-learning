package com.xiaoming.day35;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 35：换真 MySQL（H2 → MySQL）—— 启动类
 *
 * ⭐ 这个类**一个字都没改**（Day34 → Day35），这正是今天想让你看到的第一件事：
 *   **换数据库不用动 Java 代码** —— 只动三样东西：
 *     ① pom.xml 里的驱动依赖（TODO①）
 *     ② application.properties 里的 url / driver / 用户名密码（TODO②）
 *     ③ 建表语句（TODO③：从 Java 里的 DDL 搬进 schema.sql）
 *   业务代码（Controller / Service / Repository）**全部照旧跑**，
 *   因为 JDBC 把"连的是哪个数据库"这件事**抽象掉了**。
 *
 * ⭐ 启动类上那两件"看不见的自动装配"（Day34 就有的）：
 *
 *   ① **DataSource**：pom 里有 spring-boot-starter-jdbc + 一个驱动，
 *      SpringBoot 看到 application.properties 里的 spring.datasource.url
 *      就自动建好了一个 **HikariCP 连接池** + 一个 `JdbcTemplate` Bean（直接注入就能用）。
 *      ⭐ Day35 起你要能回答：**它凭什么知道该用哪个驱动？**（看 classpath / url 前缀）
 *
 *   ② **TransactionManager（事务管理器）**：有 DataSourceAutoConfiguration 就会顺带建一个
 *      `JdbcTransactionManager`（Day34 日志里那句 `Creating new transaction ...` 就是它打的）
 *      —— ⭐ 它才是真正执行 `commit()` / `rollback()` 的人。
 *
 *   ⭐ 下面这行注解**故意注释掉**了：
 *      @EnableTransactionManagement
 *      它的作用是"开启 @Transactional 注解支持"，但 SpringBoot 的自动装配已经替你加了，
 *      所以**写了也能跑、不写也能跑**。放这儿是为了让你"亲眼看到有这个东西存在"。
 *
 * @SpringBootApplication 的位置还是老规矩：从 com.xiaoming.day35 往下扫描所有子包
 * （Day35 新增的类：db 包下面的 SchemaController）
 */
// @EnableTransactionManagement        // ← 想验证"是不是真的自动开了"？取消注释，行为一模一样
@SpringBootApplication
public class Day35Application {

    public static void main(String[] args) {
        SpringApplication.run(Day35Application.class, args);
    }
}