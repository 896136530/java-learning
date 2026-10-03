package com.xiaoming.day30.config;

import com.xiaoming.day30.entity.Task;
import com.xiaoming.day30.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时灌入 8 条测试数据（已写好，别改）
 *
 * ⭐ 这是"程序启动后自动执行一次"的写法：实现 CommandLineRunner 接口
 *   （以前你得在 main 里手动调用一个 setup() 方法；现在 Spring 启动完自动喊它）
 *
 * ⚠️ 为什么要灌数据？因为 H2 是**内存数据库**：程序一关，数据就没了（见 application.properties 的说明）。
 *    每次启动都重新灌一遍，保证你打开浏览器就能看到数据、不用手动录。
 *
 * 📌 对比 Day26~29：你在 tool.Db / Setup 里写的 initTable()，就是这个东西的"手工版"。
 *    SpringBoot 里对应的是 CommandLineRunner（或 SQL 初始化脚本 schema.sql/data.sql）。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TaskRepository taskRepository;

    public DataInitializer(TaskRepository taskRepository) {   // ⭐ 构造器注入（比 @Autowired 更推荐）
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (taskRepository.count() > 0) {
            return;                       // 已经有数据就不重复灌
        }

        List<Task> seed = List.of(
                new Task("Java 集合与泛型", 1, "Java", true),
                new Task("Java 异常与 IO", 2, "Java", true),
                new Task("MySQL 增删改查", 3, "Java", true),
                new Task("JDBC 与 DAO 分层", 4, "Java", true),
                new Task("Maven 与项目结构", 5, "Java", true),
                new Task("Python 字符串进阶", 3, "Python", false),
                new Task("蓝桥真题：模拟赛一套", 4, "算法", false),
                new Task("记账本 Step3：报表功能", 5, "项目", false));

        taskRepository.saveAll(seed);

        // ⭐ 为什么用 log 而不是 System.out.println？
        //    System.out 的输出可能被缓冲（第一次验证时这行就没打出来，害我以为没执行），
        //    而 log 走的是 SpringBoot 的日志系统，**一定会按顺序出现在启动日志里**。
        //    📌 经验：项目里尽量用日志，别用 System.out —— 日志能分级、能控开关、能写文件。
        log.info("===== 已灌入测试数据 {} 条 =====", seed.size());
        log.info("浏览器打开：http://localhost:8080/tasks");
    }
}