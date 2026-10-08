package com.xiaoming.day31;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Day 31：全局异常处理 + 统一响应 —— 启动类
 *
 * ⭐ 和 Day30 的启动类一模一样（只改了个名字）。
 *    这说明今天学的不是"新框架"，而是"怎么把 Day30 那个项目做得**专业**"：
 *      Day30：接口能返回数据就算成功 → 但**出错时**返回一片空白，前端没法处理
 *      Day31：所有"出错"都变成**格式统一的 JSON + 正确的 HTTP 状态码**
 *
 * @SpringBootApplication 的位置还是老规矩：
 *   从 com.xiaoming.day31 往下扫描所有子包（controller / service / common ...）
 */
@SpringBootApplication
public class Day31Application {

    public static void main(String[] args) {
        SpringApplication.run(Day31Application.class, args);
    }
}