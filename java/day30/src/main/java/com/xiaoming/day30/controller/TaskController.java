package com.xiaoming.day30.controller;

import com.xiaoming.day30.entity.Task;
import com.xiaoming.day30.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务接口（Controller）—— 今天的重头戏
 *
 * ⭐ 什么叫"接口"？以前你的程序是：
 *     main() 里 System.out.println(...) 打印给人看
 *   现在是：
 *     别人（浏览器/前端/手机）发一个 HTTP 请求过来，你返回 **数据（JSON）**
 *     → 这就是前后端分离：后端只管给数据，长什么样由前端决定
 *
 * ⭐ 你**一个 HTTP 服务器都没写**，但下面这些 URL 已经能访问了：
 *     因为 @RestController + 内嵌 Tomcat 全替你做了
 *
 * ─────────── 今天写好的 6 个接口（用浏览器/curl 直接访问）───────────
 *   GET  /tasks                    → 所有任务（JSON 数组）
 *   GET  /tasks/{id}               → 某一个任务
 *   GET  /tasks?day=3              → 第 3 天的任务
 *   GET  /tasks/done               → 已完成的任务
 *   GET  /tasks/report             → 统计报告（总数/完成数/完成率）
 *   POST /tasks                    → 新增一个任务（要发 JSON 数据，浏览器地址栏不行）
 *
 * ⭐ 三个注解就这几个意思：
 *   @RestController  = 这个类的方法返回值直接当"响应内容"（自动转 JSON）
 *   @RequestMapping  = 这类接口的统一前缀（/tasks）
 *   @GetMapping      = 处理 GET 请求；@PostMapping = 处理 POST 请求
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    /**
     * ⭐ "依赖注入"（DI）—— SpringBoot 最核心的概念，今天只需要会"用"：
     *
     * 你没写 `new TaskRepositoryImpl()`，为什么能用？
     *   因为 Spring 启动时：① 扫描到 TaskRepository 这个接口
     *                        ② 用 Spring Data JPA 生成它的实现类
     *                        ③ 创建好对象，放进"容器"（Spring 的物件仓库）
     *                        ④ 看到 @Autowired，就把那个对象**塞给你**
     *
     * 一句话：**你声明"我需要什么"，Spring 负责"给你什么"。**
     * 好处：以后想换实现（换成 MySQL / 换成 Mock），改配置就行，Controller 一行都不用改。
     */
    @Autowired
    private TaskRepository taskRepository;

    /** ① 所有任务 → GET /tasks */
    @GetMapping
    public List<Task> list() {
        return taskRepository.findAll();
    }

    /** ② 某一天的任务 → GET /tasks?day=3 */
    @GetMapping(params = "day")
    public List<Task> listByDay(@RequestParam("day") Integer day) {
        return taskRepository.findByDayNo(day);
    }

    /** ③ 已完成的任务 → GET /tasks/done */
    @GetMapping("/done")
    public List<Task> done() {
        return taskRepository.findByDone(Boolean.TRUE);
    }

    /** ④ 某一个任务 → GET /tasks/3 */
    @GetMapping("/{id}")
    public Task one(@PathVariable("id") Long id) {
        // ⭐ TODO③（你写）：查不到怎么办？
        //    明天（Day31）会教正规写法（抛异常 + 统一错误响应）。
        //    今天先用最简单的：查不到就返回 null（浏览器会看到一片空白 —— 你先感受一下这个体验有多差）
        return taskRepository.findById(id).orElse(null);
    }

    /** ⑤ 统计报告 → GET /tasks/report */
    @GetMapping("/report")
    public Map<String, Object> report() {
        long total = taskRepository.count();
        long finished = taskRepository.countByDone(Boolean.TRUE);

        // ⭐ TODO②（你写）：算出完成率（保留 1 位小数，例如 62.5）
        //    提示：total 为 0 时要避免除以 0；保留 1 位小数可以用
        //         Math.round(rate * 10) / 10.0
        double rate = 0.0;

        // Map 转成 JSON 就是一个对象：{"total":8,"finished":5,"rate":62.5}
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("finished", finished);
        result.put("rate", rate);

        // ⭐ TODO⑤（你写）：把 application.properties 里的 app.name 读出来放进报告
        //    做法：在这个类的字段上加 @Value("${app.name:未配置}")
        //          （冒号后面的"未配置"是**默认值**——配置项没打开时就用它，这样不会启动失败）
        //    然后把那个字段 put 进 result，键名叫 "appName"
        // result.put("appName", appName);

        // ⭐ TODO⑥（附加题）：用 pom 里刚加的 commons-lang3 数"所有标题的总字数"
        //    ① 先在文件顶部 import org.apache.commons.lang3.???;（包名自己拼）
        //    ② 拿到所有任务，把标题拼成一个长字符串
        //       提示：taskRepository.findAll()  →  遍历/stream 取 getTitle()  →  拼起来
        //    ③ 用 StringUtils.length(那个长串) 数长度，put 进 result，键名 "totalChars"
        //    期望：自己跑出来看那个数字（8 个标题加起来）
        // result.put("totalChars", ？？？);

        return result;
    }

    /**
     * ⑥ 新增任务 → POST /tasks
     *    请求体（JSON）：{"title":"写 Day31 作业","dayNo":6,"type":"Java","done":false}
     *
     * ⭐ @RequestBody = "把请求体里的 JSON 自动变成 Task 对象"（靠 Jackson）
     *    以前你要自己解析字符串，现在一行注解搞定。
     */
    @PostMapping
    public Task add(@RequestBody Task task) {
        // ⭐ TODO④（你写）：保存并返回。提示：save 方法已经在 taskRepository 里了
        //    注意：save 返回的是"保存后的对象"（带上了数据库生成的自增 id），要把它 return 出去
        return null;
    }

    // ══════════════ 下面是"送分观察题"，不用写代码 ══════════════
    //
    // ⑦ 【观察题 A】故意访问一个不存在的地址，比如 http://localhost:8080/abc
    //    → 你会看到 SpringBoot 的 **Whitelabel Error Page**（404 页面）
    //    → 想一想：这个页面是谁给你的？你写过吗？（答案：SpringBoot 自带的默认错误页）
    //
    // ⑧ 【观察题 B】把某个 TODO 写错（比如把 @GetMapping 写成 @GetMaping，少个 p）
    //    → 看启动时的报错：编译不过 → 说明"注解也是类，也有拼写检查"
    //    → 再故意把 @RequestMapping("/tasks") 改成 "/task"
    //    → 浏览器访问 /tasks 就 404 了 —— 体会一下"改一行配置，接口地址全变"
    //
    // ⑨ 【观察题 C】把 application.properties 里的 port 从 8080 改成 8081，重启
    //    → 看启动日志那行 "Tomcat started on port 8081"
    //    → 想一下：以前用 MySQL 要改端口，得去改哪个文件？（my.ini）
    //      现在改端口只改一行 properties —— **这就是"配置外置"的价值**
}