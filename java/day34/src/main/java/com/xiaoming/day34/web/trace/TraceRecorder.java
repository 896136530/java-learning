package com.xiaoming.day34.web.trace;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * ⭐⭐ 今天的主角工具之一：**执行轨迹记录器** —— 把"这一路上谁先谁后"记下来给你看
 *
 * 【本类索引：3 个 ThreadLocal 操作 + 1 个历史队列】
 *   ┌──────────────────────────────┬───────────────────────────────────────────────┐
 *   │ record(String step)          │ 往"**当前请求**"的轨迹里加一行                  │
 *   │ steps()                      │ 读出"当前请求"的轨迹（复制一份，别让外面改到）    │
 *   │ publish(...)                 │ 请求结束时，把这份轨迹存进 history（给 /trace/last 看）│
 *   │ lastCompleted()              │ 取最近一次"已完成"的轨迹                        │
 *   │ clear()                      │ ⭐ **清掉当前线程的轨迹**（必须在 afterCompletion 里调）│
 *   └──────────────────────────────┴───────────────────────────────────────────────┘
 *
 * 【为什么需要它？】
 *   今天要学的"执行顺序"光靠嘴说没用。所以做了个探针：
 *   过滤器、拦截器每走到一步就记一行，最后 `/trace/last` 把整条轨迹吐出来 ——
 *   **顺序是对是错，一眼就能看见**（这也是"期望值必须实测"的另一种用法）。
 *
 * 【⭐ 新东西：ThreadLocal —— "每个线程自己的储物柜"】
 *   ```java
 *   private final ThreadLocal<List<String>> current = new ThreadLocal<>();
 *   current.set(list);      // 把东西放进"当前线程"的柜子
 *   current.get();          // 只有当前线程取得出来，别的线程 get() 是 null
 *   current.remove();       // 把柜子清空（⭐ 不清就是内存泄漏 + 串数据）
 *   ```
 *   为什么需要它？—— 因为 Filter / Interceptor **都是单例**（全程只有一个对象），
 *   不能把"这次请求的轨迹"存成字段（并发下会串成一锅粥，Day18/19 学过）。
 *   而每个 HTTP 请求由 **一个 Tomcat 线程**从头跑到尾 →
 *   "一个请求"和"一个线程"是绑定的 → 所以用 ThreadLocal 存"请求级数据"刚刚好。
 *
 *   ⚠️⚠️ 但有一件事必须记住：**Tomcat 的线程是复用的**（线程池，100 个请求可能只用 5 个线程）。
 *   所以你 `set` 进去的东西，如果不在请求结束时 `remove()`，
 *   **下一个请求（同一个线程）会看到上一个请求留下的东西** ——
 *   这就是"越权事故"的经典成因。Day33 题目第 7 步就是修它。
 */
@Component
public class TraceRecorder {

    /** 只保留最近 5 次"已完成"的轨迹，避免内存无限长 */
    private static final int KEEP = 5;

    /**
     * ⭐ ThreadLocal：每个线程一份自己的 List。
     *   注意这里**故意不用** `ThreadLocal.withInitial(...)`（那种写法不会把"清空"暴露出来），
     *   就是要让你亲手写 clear()，才看得见"没清会怎样"。
     */
    private final ThreadLocal<List<String>> current = new ThreadLocal<>();

    /** 最近几次"已完成"的轨迹（Deque = 双端队列，从尾巴进、从头上淘汰） */
    private final Deque<Snapshot> history = new ConcurrentLinkedDeque<>();

    /** 往当前请求的轨迹里加一行 */
    public void record(String step) {
        List<String> list = current.get();
        if (list == null) {
            list = new ArrayList<>();
            current.set(list);
        }
        list.add(step);
    }

    /** 读出当前请求的轨迹（复制一份再给出去，防止外面顺手改到内部状态） */
    public List<String> steps() {
        List<String> list = current.get();
        return (list == null) ? new ArrayList<>() : new ArrayList<>(list);
    }

    /**
     * 请求结束时调用：把当前轨迹做成快照存进 history。
     * ⭐ 必须在 clear() **之前**调用 —— 先存档，再清柜子。
     */
    public void publish(String method, String uri, int status, long costMs) {
        history.addLast(new Snapshot(Thread.currentThread().getName(), method, uri, status, costMs, steps()));
        while (history.size() > KEEP) {
            history.pollFirst();
        }
    }

    /** 最近一次"已完成"的请求轨迹（没跑过任何请求时是 null） */
    public Snapshot lastCompleted() {
        return history.peekLast();
    }

    /**
     * ⭐⭐ 清掉当前线程的轨迹 —— **必须在拦截器的 afterCompletion 里调**。
     * 不调的后果（真机实测，见 Day33 练习题第 7 步）：下一个请求打到**同一个线程**时，
     * 轨迹里会带上一次的残留，越滚越长。
     */
    public void clear() {
        current.remove();
    }

    /**
     * 一次请求的轨迹快照（普通 POJO，Jackson 会按 getter 转成 JSON）
     */
    public static class Snapshot {

        private final String thread;
        private final String method;
        private final String uri;
        private final int status;
        private final long costMs;
        private final List<String> steps;

        public Snapshot(String thread, String method, String uri, int status, long costMs, List<String> steps) {
            this.thread = thread;
            this.method = method;
            this.uri = uri;
            this.status = status;
            this.costMs = costMs;
            this.steps = steps;
        }

        public String getThread() { return thread; }
        public String getMethod() { return method; }
        public String getUri() { return uri; }
        public int getStatus() { return status; }
        public long getCostMs() { return costMs; }
        public List<String> getSteps() { return steps; }
    }
}