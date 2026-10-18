package com.xiaoming.day34.order;

import com.xiaoming.day34.common.Result;
import jakarta.validation.constraints.Min;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 下单接口层（Controller）—— 8 个"看起来差不多"的接口，**返回几乎一样，数据库完全不同**
 *
 * 【本类索引：8 个 POST + 1 个 GET】
 *   ┌──────────────────────────────────┬──────────────────────────────────────┬────────┐
 *   │ 接口                              │ 它演的是哪一出                        │ 步骤    │
 *   ├──────────────────────────────────┼──────────────────────────────────────┼────────┤
 *   │ POST /order/place                │ 正规入口（TODO① 加注解、TODO③ 加回滚范围）│ 第 3、5步│
 *   │ POST /order/place-selfcall       │ ⭐⭐ 同类 this 调用 → 静默失效         │ 第 4 步 │
 *   │ POST /order/place-swallow        │ 异常被吞 → 提交脏数据                  │ 第 6 步 │
 *   │ POST /order/place-audit          │ 传播行为 REQUIRED vs REQUIRES_NEW      │ 第 7 步 │
 *   │ POST /order/place-readonly       │ readOnly=true 却在写（TODO⑥）          │ 第 8 步 │
 *   │ POST /order/place-package-private│ 方法不是 public（实测）                │ 第 8 步 │
 *   │ POST /order/place-new            │ 类不是 Bean（自己 new，没有代理）       │ 第 8 步 │
 *   │ POST /order/place-on-controller  │ @Transactional 写在 Controller 上      │ 第 8 步 │
 *   │ GET  /order/list                 │ 看订单表内容（等价 /db/orders）         │ 通用    │
 *   └──────────────────────────────────┴──────────────────────────────────────┴────────┘
 *
 * 【⭐ 调用参数说明（全部是 URL 上的参数，方便你用浏览器/PowerShell 直接打）】
 *   skuId  买哪个商品（1 / 2 / 3）
 *   count  买几件（种子数据：1 号库存 10、2 号库存 3、3 号库存 0）
 *   fail   只对 /place 和 /place-selfcall 有效：none（默认）/ runtime / checked
 *
 * 【⭐ 顺序提醒：`@Validated`（类级）还是 Day32 那个作用】
 *   它管的是"方法参数校验"（?count=0 → 400）。**它和事务毫无关系** ——
 *   一个是"进门前的检查"（拦在方法外），一个是"办事的账本"（包在方法外"更里面"一层）。
 */
@RestController
@RequestMapping("/order")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** ① 正规下单 → POST /order/place?skuId=1&count=2&fail=runtime */
    @PostMapping("/place")
    public Result<Order> place(@RequestParam("skuId") Long skuId,
                               @RequestParam(value = "count", defaultValue = "1")
                               @Min(value = 1, message = "下单数量至少是 1") int count,
                               @RequestParam(value = "fail", defaultValue = "none") String fail)
            throws ExternalServiceException {
        return Result.ok(orderService.place(skuId, count, fail));
    }

    /** ② ⭐⭐ 主坑入口 → POST /order/place-selfcall?skuId=1&count=2&fail=runtime */
    @PostMapping("/place-selfcall")
    public Result<Order> placeViaSelfCall(@RequestParam("skuId") Long skuId,
                                          @RequestParam(value = "count", defaultValue = "1") int count,
                                          @RequestParam(value = "fail", defaultValue = "runtime") String fail)
            throws ExternalServiceException {
        return Result.ok(orderService.placeViaSelfCall(skuId, count, fail));
    }

    /** ③ 吞异常 → POST /order/place-swallow?skuId=1&count=2 */
    @PostMapping("/place-swallow")
    public Result<Order> placeSwallow(@RequestParam("skuId") Long skuId,
                                      @RequestParam(value = "count", defaultValue = "1") int count) {
        return Result.ok(orderService.placeSwallow(skuId, count));
    }

    /** ④ 传播行为 → POST /order/place-audit?skuId=1&count=2 */
    @PostMapping("/place-audit")
    public Result<Order> placeWithAudit(@RequestParam("skuId") Long skuId,
                                        @RequestParam(value = "count", defaultValue = "1") int count) {
        return Result.ok(orderService.placeWithAudit(skuId, count));
    }

    /** ⑤ readOnly 实验 → POST /order/place-readonly?skuId=1&count=2 */
    @PostMapping("/place-readonly")
    public Result<Order> placeReadOnly(@RequestParam("skuId") Long skuId,
                                       @RequestParam(value = "count", defaultValue = "1") int count) {
        return Result.ok(orderService.placeReadOnly(skuId, count));
    }

    /**
     * ⑥ 方法不是 public 的实验 → POST /order/place-package-private?skuId=1&count=2
     *
     * ⭐ 注意这一行能编译过，是因为 `OrderController` 和 `OrderService` **在同一个包**里
     *    （com.xiaoming.day34.order）—— 包级权限（default）只在同包可见。
     *    如果这个 Controller 放到别的包，这里就编译不过了。
     */
    @PostMapping("/place-package-private")
    public Result<Order> placePackagePrivate(@RequestParam("skuId") Long skuId,
                                             @RequestParam(value = "count", defaultValue = "1") int count) {
        return Result.ok(orderService.placePackagePrivate(skuId, count));
    }

    /** ⑦ 类不是 Bean 的实验 → POST /order/place-new?skuId=1&count=2 */
    @PostMapping("/place-new")
    public Result<Order> placeWithManualNew(@RequestParam("skuId") Long skuId,
                                            @RequestParam(value = "count", defaultValue = "1") int count) {
        return Result.ok(orderService.placeWithManualNew(skuId, count));
    }

    /**
     * ⑧ ⭐ 反直觉实验：`@Transactional` 直接写在 **Controller** 的方法上
     *    → POST /order/place-on-controller?skuId=1&count=2
     *
     * 大家都听过"事务要写在 Service，不要写在 Controller"。
     * 那写在 Controller 上**到底会不会回滚**？—— 别猜，实测（第 8 步填表）。
     *
     * ⭐ 提示：这里调的是 `orderService.place(..., "runtime")`。
     *    交付版的 place() **没有**任何事务注解（TODO① 还没做），
     *    所以如果这个接口能干净回滚，那功劳就只能是这一行注解的。
     *    （事务是**线程级**的：Controller 开的这个事务，会盖住同一条线程上 Service 里的所有 SQL。）
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/place-on-controller")
    public Result<Order> placeOnController(@RequestParam("skuId") Long skuId,
                                           @RequestParam(value = "count", defaultValue = "1") int count)
            throws ExternalServiceException {
        return Result.ok(orderService.place(skuId, count, "runtime"));
    }

    /** ⑨ 看订单表 → GET /order/list */
    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.ok(orderService.list());
    }
}