package com.xiaoming.day33;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 33：拦截器 / 过滤器 —— 启动类
 *
 * ⭐ 和 Day30/31/32 的启动类一模一样（只改了名字）。
 *    这四天其实是**同一个项目在长大**：
 *      Day30：把接口跑起来（能返回 JSON 就算赢）
 *      Day31：把"出错"变专业（统一异常 + 统一响应）
 *      Day32：把"入口"变专业（参数校验，别让脏数据进来）
 *      Day33：把"路上的关卡"变专业（Filter / Interceptor：谁进谁出、记日志、查登录）
 *
 * @SpringBootApplication 的位置还是老规矩：
 *   从 com.xiaoming.day33 往下扫描所有子包
 *   （今天多出来的包：web.filter / web.interceptor / web.trace / auth / publicapi）
 *
 * 📌 今天**没有**加 @ServletComponentScan —— 因为本项目用的是
 *    FilterRegistrationBean 注册过滤器（能精确控制顺序）。
 *    另一种写法是 @WebFilter + @ServletComponentScan（见知识点第 7 节）。
 */
@SpringBootApplication
public class Day33Application {

    public static void main(String[] args) {
        SpringApplication.run(Day33Application.class, args);
    }
}