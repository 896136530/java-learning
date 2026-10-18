package com.xiaoming.day34.order;

import com.xiaoming.day34.common.exception.BizRuleException;
import com.xiaoming.day34.common.exception.ParamException;
import com.xiaoming.day34.common.exception.ResourceNotFoundException;
import com.xiaoming.day34.sku.Sku;
import com.xiaoming.day34.sku.SkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ⭐⭐⭐ 下单业务层 —— **今天所有事故的现场**（一个类里排了 8 种"事务到底生不生效"的写法）
 *
 * 【本类索引：8 个"入口方法" + 2 个私有零件】
 *   ┌────────────────────────────┬──────────────────────────────────────────┬────────────┐
 *   │ 方法                        │ 交付版的事务状态（就是要你去发现的）        │ 对应步骤    │
 *   ├────────────────────────────┼──────────────────────────────────────────┼────────────┤
 *   │ place(...)                 │ ❌ 压根没加注解                     TODO①  │ 第 3、5 步 │
 *   │ placeViaSelfCall(...)      │ ❌ 同类 this 调用，绕过代理         TODO②  │ 第 4 步 ⭐⭐ │
 *   │ placeSwallow(...)          │ ❌ 异常被自己 catch 吞掉            TODO④  │ 第 6 步    │
 *   │ placeWithAudit(...)        │ ⚠️ 关键在 AuditService 的传播行为   TODO⑤  │ 第 7 步    │
 *   │ placeReadOnly(...)         │ ⚠️ readOnly=true 却在写库          TODO⑥  │ 第 8 步    │
 *   │ placePackagePrivate(...)   │ ？方法不是 public（真机实测）               │ 第 8 步    │
 *   │ placeWithManualNew(...)    │ ❌ 自己 new 出来的对象没有代理              │ 第 8 步    │
 *   │ placeOnController(...)     │ 写在 Controller 上（真机实测，反直觉）      │ 第 8 步    │
 *   ├────────────────────────────┼──────────────────────────────────────────┼────────────┤
 *   │ writeOrderAndStock(...)    │ 私有零件：写订单 + 扣库存（"一次业务的两半"）│ 公共        │
 *   │ maybeFail(...)             │ 私有零件：故意出错的开关（none/runtime/checked）│ 公共     │
 *   └────────────────────────────┴──────────────────────────────────────────┴────────────┘
 *
 * 【⭐⭐⭐ 今天最想让你看到的一件事：同样是"下单失败"，数据库可能完全不同】
 *   下面这 4 个接口，**返回给你看的响应几乎一样**（409 + 一句"模拟失败"）：
 *     /order/place?fail=runtime          → 干净回滚？还是脏数据？
 *     /order/place-selfcall?fail=runtime → 干净回滚？还是脏数据？
 *     /order/place-swallow               → 200 + 订单 JSON，然后呢？
 *     /order/place-audit                 → 409，审计日志还在吗？
 *   光看接口**永远看不出来** —— 必须回数据库数行数（`GET /db/state`）。
 *   ⭐ 这就是今天的主线：**"静默失效"——它不报错、不告警，只是悄悄把错误的数据留在库里。**
 *
 * 【⭐ 一个必须记住的顺序】
 *   事务拦截器（TransactionInterceptor）**包在 Controller 调用你的 Service 之间**：
 *     Controller → 【代理】→ 事务拦截器（开事务）→ 你的方法 → 【提交/回滚】
 *   所以"事务"这件事发生在**比 Controller 更靠里**的一层 —— 这也解释了后面第 2 步那道题。
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final SkuRepository skuRepository;
    private final AuditService auditService;

    /** 构造器注入（Day33 学的姿势）。AuditService 是**另一个 Bean**，第 7 步要靠它 */
    public OrderService(OrderRepository orderRepository, SkuRepository skuRepository, AuditService auditService) {
        this.orderRepository = orderRepository;
        this.skuRepository = skuRepository;
        this.auditService = auditService;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ① 正规入口：下单（Controller 直接调的就是它）
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 下单 = ① 往 orders 插一行 ② 把 sku.stock 减掉 ③ 可能失败
     *
     * ⭐⭐⭐ TODO①（第 3 步）：**这个方法现在完全没有事务** —— 请你在它上面加一个注解。
     *
     *   ┌─ 你要写的东西（就一行）─────────────────────────────────────────────────┐
     *   │ @Transactional                                                          │
     *   └─────────────────────────────────────────────────────────────────────────┘
     *
     *   · 这个注解加在**方法上**（也可以加类上，那是"这个类所有 public 方法都开事务"）
     *   · 加在哪一层？—— **Service 层**。不是 Controller（太靠外，事务范围会变得很大），
     *     也不是 Repository（太靠里，一次业务要改两张表，单个 SQL 的事务没意义）
     *   · `org.springframework.transaction.annotation.Transactional` —— 注意别 import 错了包
     *     （IDEA 有时会提示 `jakarta.transaction.Transactional`，那是另一套规范，今天不用）
     *
     * ⭐⭐ TODO③（第 5 步）：等 TODO① 写完，你会发现"受检异常还是不回滚"（为什么？）。
     *   到时候把同一个注解改成：
     *
     *   ┌─ 最终形态 ──────────────────────────────────────────────────────────────┐
     *   │ @Transactional(rollbackFor = Exception.class)                            │
     *   └─────────────────────────────────────────────────────────────────────────┘
     *
     *   ⭐ 一句话记住：**想让它"出了任何错都别留脏数据"，就写 rollbackFor = Exception.class。**
     *      （代价是"预期内的业务异常也回滚" —— 但绝大多数业务场景要的就是这个。）
     *
     * @param fail none = 正常下单；runtime = 抛运行时异常；checked = 抛受检异常
     */
    public Order place(Long skuId, int count, String fail) throws ExternalServiceException {
        Order order = writeOrderAndStock(skuId, count);
        maybeFail(fail);
        return order;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ② ⭐⭐ 主坑：同类内部调用（this.xxx）—— 事务**静默失效**
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 一个"看起来只是转发一下"的入口。
     *
     * ⭐⭐⭐ TODO②（第 4 步 · **今天最重要的一个坑**）：
     *   现在这个方法体是 `return this.place(skuId, count, fail);`
     *   —— 它**不会**回滚。请你想清楚"为什么"，然后修好它（改法不止一种，见题目第 4 步）。
     *
     *   ┌─ 观察顺序（照做，别跳）───────────────────────────────────────────────────┐
     *   │ 1. `POST /db/reset`                     复位                                      │
     *   │ 2. `POST /order/place-selfcall?skuId=1&count=2&fail=runtime`                      │
     *   │    → 看响应：409 + 「模拟：下单后调用内部风控服务被拒」                              │
     *   │      ⭐ 这句话会让你以为"业务被拒绝了，什么都没发生"                                 │
     *   │ 3. `GET /db/state`                                                                │
     *   │    → orders 是多少？SKU-0001 的 stock 是多少？                                     │
     *   │ 4. 再跑一遍步骤 1~3，但把路径换成 `/order/place`（TODO① 修完之后）                   │
     *   │    → 对比两个结果，你就亲眼看到"代理"这两个字值多少钱了                              │
     *   └──────────────────────────────────────────────────────────────────────────┘
     */
    public Order placeViaSelfCall(Long skuId, int count, String fail) throws ExternalServiceException {
        // ⭐ TODO②：问题就出在这一行。修法（任选一种，说明理由即可）：
        //    A. 给"对外入口"自己加 @Transactional（最常用：谁是对外入口，谁负责开事务）
        //    B. 把真正的事务方法拆到**另一个 Bean**，通过注入调用（走代理，最彻底）
        //    C. 自注入 / AopContext.currentProxy()（能跑，但绕，真实项目少见）
        return this.place(skuId, count, fail);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ③ 异常被自己 catch 掉了
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 下单，但"失败"这件事被自己吞了（日志记一下，然后正常返回）。
     *
     * ⭐⭐⭐ TODO④（第 6 步）：先按下面两步观察，再修。
     *
     *   ┌─ 观察 ──────────────────────────────────────────────────────────────────┐
     *   │ 1. `POST /db/reset`                                                       │
     *   │ 2. `POST /order/place-swallow?skuId=1&count=2`                             │
     *   │    → 响应 **200**，还带着一张订单 JSON（看起来"下单成功"了！）                │
     *   │ 3. `GET /db/state`                                                        │
     *   │    → orders=1、SKU-0001 的 stock=8 —— **数据已经写进去了**                  │
     *   │      ⭐ 但你的 catch 里写着"下单失败"…… 那这单到底成没成？                   │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *
     *   ┌─ 你要写的东西（catch 块里补一行）──────────────────────────────────────────┐
     *   │ TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();    │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *
     *   ⭐ 为什么这一行有用？—— 因为 Spring 判断"要不要回滚"看的**不是你心里怎么想**，
     *      而是"异常有没有继续往外抛"。你 catch 住了，它就觉得"一切正常，提交吧"。
     *      `setRollbackOnly()` 就是那句"我虽然没抛出异常，但请务必别提交"。
     *   ⭐ 另一个可行的修法是**把异常重新抛出去**（`throw e;`），效果也是回滚 ——
     *      区别是接口会变成 409/500。两种都算对，题目第 6 步给了实测对照。
     */
    @Transactional(rollbackFor = Exception.class)
    public Order placeSwallow(Long skuId, int count) {
        Order order = writeOrderAndStock(skuId, count);
        try {
            // 模拟"下单后半段"要做的一件事失败了（比如加积分、发短信、回调支付）
            throw new BizRuleException("模拟：下单后调用内部积分服务失败");
        } catch (BizRuleException e) {
            log.warn("[swallow] 异常被我 catch 了，没有往外抛，只记了一行日志：{}", e.getMessage());
            // ⭐ TODO④：这里少了一句"请务必回滚"
            return order;      // ← 正常返回 → 事务**提交** → 脏数据留在库里
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ④ 传播行为：主业务回滚了，审计日志要不要留？
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 下单 + 记一条审计日志，然后**故意失败**。
     * 问题：审计日志应该跟着一起回滚吗？
     *
     * ⭐ TODO⑤（第 7 步）：要改的代码**不在这个方法里**，在
     *   `AuditService.writeLog(...)` 的 `propagation` 上（去那个文件看 TODO⑤）。
     *
     *   ┌─ 观察 ──────────────────────────────────────────────────────────────────┐
     *   │ 1. `POST /db/reset`                                                       │
     *   │ 2. `POST /order/place-audit?skuId=1&count=2`                               │
     *   │    → 响应 409 + 「模拟：下单后半段失败（审计日志已经写完了）」                  │
     *   │ 3. `GET /db/state`                                                        │
     *   │    → orders 和 stock 都恢复了（回滚了）                                       │
     *   │    → 但 **auditLog 是 0 还是 1**？← 这才是本步的答案                          │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *
     *   ⭐ 业务上到底哪个对？——**审计日志必须留下**（不然出了事你连"谁试过"都查不到）。
     *      这就是"两个诉求互相矛盾，要用传播行为解决"的真实场景。
     */
    @Transactional(rollbackFor = Exception.class)
    public Order placeWithAudit(Long skuId, int count) {
        Order order = writeOrderAndStock(skuId, count);
        // ⭐ 注意：这是**跨 Bean 调用**（auditService 是另一个 Bean 的代理），
        //    所以这里的 @Transactional 传播行为是**真的会生效的** —— 和 this.xxx() 完全不同。
        auditService.writeLog("PLACE", "已写订单 id=" + order.getId() + "，金额=" + order.getAmount());
        throw new BizRuleException("模拟：下单后半段失败（审计日志已经写完了）");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ⑤ readOnly = true 加在写方法上
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * 一个"看起来是查询、其实在写库"的方法（这种代码在真实项目里**非常常见**：
     * 比如"查询订单，顺便把过期订单标记为已取消"）。
     *
     * ⭐ TODO⑥（第 8 步）：先**别改代码**，先跑：
     *     `POST /order/place-readonly?skuId=1&count=2` → 看响应 → 再 `GET /db/state`。
     *   把你看到的结果填进题目第 8 步的表格（那个表格里答案版是空的，就是要你实测）。
     *   然后回答：`readOnly = true` 到底有没有拦住这次写操作？如果拦住了，报什么错？
     *   最后把它改成正确写法（写方法不该声明 readOnly）。
     *
     *   ⭐ 提示：`readOnly = true` 的**真实用途不是"拦住你写"**，而是
     *      ① 给框架一个提示（Hibernate 会跳过脏检查、Spring 会 setReadOnly）
     *      ② 提醒下一个读代码的人"这个方法不该写库"
     *      想靠它做安全防线，是**靠不住**的。
     */
    @Transactional(readOnly = true)
    public Order placeReadOnly(Long skuId, int count) {
        return writeOrderAndStock(skuId, count);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ⑥ 方法不是 public（包级）—— 真机实测
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * ⭐ 第 8 步实验②：**包级（没有 public / private / protected 修饰符）**的事务方法。
     *
     * 它会从 `OrderController` 里被调用（同一个包 com.xiaoming.day34.order 才能调包级方法），
     * 所以这一次**不是** self-call 的问题 —— 调用确实经过了代理。
     * 那它到底开不开事务？**老博客都说"不是 public 就不生效"。今天实测给你看。**
     *
     * 📌 顺便解释"为什么 private 没法单独测"：private 方法**只能被同一个类调用**，
     *    所以就必然同时踩上 `this.xxx()` 那个坑 —— 两个原因混在一起，分不出是谁的锅。
     *    这是实话，题目第 8 步会明确写"private 在本项目里没单独实测"。
     */
    @Transactional(rollbackFor = Exception.class)
    Order placePackagePrivate(Long skuId, int count) {
        Order order = writeOrderAndStock(skuId, count);
        throw new BizRuleException("模拟：包级事务方法里抛异常");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ⑦ 类不是 Spring Bean（自己 new 出来的对象没有代理）
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * ⭐ 第 8 步实验③：调一个**自己 new 出来**的 Service（`OrderServiceManual`，它没有 @Service）。
     *
     * 注意：这一次**不是** self-call 的问题 —— 调用的是**另一个对象**，
     * 问题在于"那个对象的 @Transactional 根本没人给它做代理"。
     */
    public Order placeWithManualNew(Long skuId, int count) {
        // ⭐ 手动装配一个"野生 Service"（它有 @Transactional 注解，但没被 Spring 管）
        OrderServiceManual manual = new OrderServiceManual(orderRepository, skuRepository);
        return manual.place(skuId, count);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  下面这些是"零件"，不对外暴露（Controller 里没有对应接口）
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * ⭐ 一次下单业务的**两半**：写订单 + 扣库存。
     *
     * 【为什么写成私有方法？—— 这是今天"隔离变量"的关键手法】
     *   它**故意不带任何注解**：这样每种"失效写法"都可以复用它，
     *   从而保证它们之间的差别**只有一个变量 = 事务到底开没开**。
     *   （如果我给每个变体都复制一份写库代码，万一哪份写错了一个字，
     *     实验结论就不可信了。）
     *
     * 【⭐ 顺便记住一件事：事务不是"方法级"的，是"线程级"的】
     *   事务信息存在 ThreadLocal 里（`TransactionSynchronizationManager`）。
     *   所以只要本次调用**在同一条线程上**，你在私有方法里执行的 SQL
     *   照样属于外层那个事务 —— 私有方法**不需要**（也不可能）加 @Transactional。
     */
    private Order writeOrderAndStock(Long skuId, int count) {
        if (count <= 0) {
            throw new ParamException("下单数量必须大于 0，你传的是 " + count);   // 400
        }
        Sku sku = skuRepository.findById(skuId);
        if (sku == null) {
            throw ResourceNotFoundException.of("SKU", skuId);                  // 404
        }
        if (sku.getStock() < count) {
            throw new BizRuleException("库存不足：「" + sku.getTitle() + "」现有 " + sku.getStock()
                    + " 件，你想下单 " + count + " 件");                          // 409
        }

        int amount = sku.getPrice() * count;
        Order order = orderRepository.insert(sku.getCode(), sku.getTitle(), sku.getId(), count, amount);
        int rows = skuRepository.decreaseStock(skuId, count);

        log.info("[order] 已写订单 id={}（{} × {} = {} 元），扣库存 {} 件（影响 {} 行），stock {} → {}",
                order.getId(), sku.getTitle(), count, amount, count, rows, sku.getStock(), sku.getStock() - count);
        return order;
    }

    /**
     * ⭐ "故意出错"的开关（也是私有零件，理由同上）
     *   none    正常返回
     *   runtime 抛**运行时异常**（BizRuleException，继承 RuntimeException）
     *   checked 抛**受检异常**（ExternalServiceException，继承 Exception）
     */
    private void maybeFail(String fail) throws ExternalServiceException {
        if ("runtime".equals(fail)) {
            throw new BizRuleException("模拟：下单后调用内部风控服务被拒（运行时异常）");
        }
        if ("checked".equals(fail)) {
            throw new ExternalServiceException("模拟：下单后调用外部积分服务超时（受检异常）");
        }
    }

    /** 查全部订单（/order/list 用） */
    public List<Order> list() {
        return orderRepository.findAll();
    }
}