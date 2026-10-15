package com.xiaoming.day32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 32：参数校验深入（分组校验 + 自定义校验注解）—— 启动类
 *
 * ⭐ 和 Day30/Day31 的启动类一模一样（只改了名字）。
 *    这三天其实是**同一个项目在长大**：
 *      Day30：把接口跑起来（能返回 JSON 就算赢）
 *      Day31：把"出错"变专业（统一异常 + 统一响应）
 *      Day32：把"入口"变专业（参数校验，别让脏数据进业务层）
 *
 * @SpringBootApplication 的位置还是老规矩：
 *   从 com.xiaoming.day32 往下扫描所有子包（sku / common / sku.valid ...）
 */
@SpringBootApplication
public class Day32Application {

    public static void main(String[] args) {
        SpringApplication.run(Day32Application.class, args);
    }
}