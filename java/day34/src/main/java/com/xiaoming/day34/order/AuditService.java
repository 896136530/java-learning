package com.xiaoming.day34.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ⭐ 审计服务（**故意做成另一个 Bean**）—— 第 7 步"传播行为"的舞台
 *
 * 【为什么必须是"另一个 Bean"？】
 *   因为传播行为只在**跨 Bean 调用**时才有机会生效：
 *     · 跨 Bean（Controller → A 的代理 → A → B 的代理 → B）→ B 上的 @Transactional 真的被读到了
 *     · 同类 this 调用（A → this.b()）→ b 上的 @Transactional **被跳过**（= 第 4 步那个坑）
 *   所以这个类虽然"只是插一行日志"，它的存在本身就是对照组的一半。
 *
 * 【ⓐ REQUIRED（默认）和 ⓑ REQUIRES_NEW 的区别（一句话）】
 *   ┌────────────────┬──────────────────────────────────────────────────────────────┐
 *   │ REQUIRED       │ "有事务就加入，没有就新建" —— **和外层同生共死**                 │
 *   │ REQUIRES_NEW   │ "挂起外层，我自己开一个全新的" —— **外层回滚跟我无关**            │
 *   └────────────────┴──────────────────────────────────────────────────────────────┘
 *   ⭐ 生活类比：REQUIRED = 夫妻共用一张卡（一起记账、一起退账）；
 *      REQUIRES_NEW = 另开一张卡自己刷（你那边退账退不到我这张卡上）。
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    /**
     * 记一条审计日志。
     *
     * ⭐⭐⭐ TODO⑤（第 7 步）：把下面的 `Propagation.REQUIRED` 改成正确的那一种。
     *
     *   ┌─ 现状（交付版）──────────────────────────────────────────────────────────┐
     *   │ @Transactional(propagation = Propagation.REQUIRED)   ← 会跟着主业务一起回滚  │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *
     *   ┌─ 你要改成（答案）────────────────────────────────────────────────────────┐
     *   │ @Transactional(propagation = Propagation.REQUIRES_NEW)                    │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *
     *   判断依据（背下来，面试常问）：
     *     "这件事**失败了也必须留下痕迹**" → REQUIRES_NEW（审计日志、操作流水、失败记录）
     *     "这件事和主业务**同生共死**"     → REQUIRED（绝大多数业务：扣款、扣库存、改状态）
     *
     *   ⚠️ 一个容易忽略的代价：REQUIRES_NEW 会**挂起外层事务、另外借一条连接**。
     *      如果外层事务已经锁住了某行数据，而内层又要去改同一行 → **死锁**。
     *      所以真实项目里 REQUIRES_NEW 只用在"改不同表"的旁路操作上（比如本类只写 audit_log）。
     *
     *   ⚠️ 另一个更隐蔽的代价（第 7 步的思考题会问）：
     *      内层这个新事务**看不到外层还没提交的数据**（H2 默认隔离级别是 READ COMMITTED）。
     *      如果主业务想在审计里带上"刚下的订单 id"，那个 id 在内层是**查不到**的 ——
     *      所以本项目是把 id 当**参数**传进来的（而不是让内层去查库）。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeLog(String action, String detail) {
        repo.insert(action, detail);
        log.info("[audit] 已记审计：action={} detail={}（当前传播行为 = REQUIRED）", action, detail);
    }
}