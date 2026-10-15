package com.xiaoming.day32.sku;

import com.xiaoming.day32.common.Result;
import com.xiaoming.day32.sku.valid.OnCreate;
import com.xiaoming.day32.sku.valid.OnUpdate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SKU 接口层（Controller）—— 今天你要改 2 个地方（TODO②）
 *
 * 【本类索引：1 个类注解 + 8 个接口，一张表看清】
 *   ┌──────┬──────────────────────────────────┬─────────────────────────────────────┐
 *   │ 方法  │ 路径 / 方式                       │ 校验靠什么                           │
 *   ├──────┼──────────────────────────────────┼─────────────────────────────────────┤
 *   │ list │ GET    /sku                       │ 无                                  │
 *   │ get  │ GET    /sku/{id}                  │ 无                                  │
 *   │ add  │ POST   /sku                       │ ⭐ TODO② 要改成 OnCreate 组           │
 *   │ upd  │ PUT    /sku                       │ ⭐ TODO② 要你写（用 OnUpdate 组）      │
 *   │ del  │ DELETE /sku/{id}                  │ 无                                  │
 *   │ out  │ POST   /sku/outbound?id=&count=   │ 无（错在 Service 里 throw）           │
 *   │ srch │ GET    /sku/search?keyword=&limit=│ ⭐ 类上的 @Validated + 参数上的 @Min/@Max│
 *   │ boom │ GET    /sku/boom                  │ 无（故意炸，验证兜底）                 │
 *   └──────┴──────────────────────────────────┴─────────────────────────────────────┘
 *
 * 【⭐⭐ 类上那个 @Validated 是今天的新东西，别和参数上的 @Valid 搞混】
 *   ┌────────────────────┬──────────────────────────────┬──────────────────────────────┐
 *   │ 放在哪              │ 管什么                        │ 校验失败抛什么                │
 *   ├────────────────────┼──────────────────────────────┼──────────────────────────────┤
 *   │ **类**上 @Validated │ **方法参数**：?limit=0、/sku/abc│ ConstraintViolationException │
 *   │                    │ （AOP 在"进方法之前"拦一道）    │ ⚠️ 全局处理器现在**没接它** → 走兜底 500│
 *   ├────────────────────┼──────────────────────────────┼──────────────────────────────┤
 *   │ **参数**上 @Valid / │ **请求体 JSON** 里的字段        │ MethodArgumentNotValidException│
 *   │ @Validated(组)      │ （把 JSON 转对象时校验）        │ （Day31 已经接住了 → 400）     │
 *   └────────────────────┴──────────────────────────────┴──────────────────────────────┘
 *   ⭐ 这两条路是**两套机制**（一个靠 AOP 代理、一个靠 SpringMVC 的参数解析），
 *      所以抛出的异常类型、取错误的方式**完全不同** —— 第 1 步你会先看到它变成 500。
 */
@RestController
@RequestMapping("/sku")
@Validated      // ⭐ 新东西：加在**类**上 → 开启"方法参数校验"（管 ?limit=0 这种，不管请求体）
public class SkuController {

    private final SkuService skuService;

    public SkuController(SkuService skuService) {
        this.skuService = skuService;
    }

    /** ① 查全部 → GET /sku */
    @GetMapping
    public Result<List<Sku>> list() {
        return Result.ok(skuService.list());
    }

    /**
     * ② 查一个 → GET /sku/999
     * ⭐ 还是**没有任何判空代码**：id=999 时 Service 直接 throw → 全局处理器 → 404
     */
    @GetMapping("/{id}")
    public Result<Sku> get(@PathVariable("id") Long id) {
        return Result.ok(skuService.getById(id));
    }

    /**
     * ③ 新增 → POST /sku （JSON 请求体）
     *
     * ⭐ TODO②：把参数上的 `@Valid` 换成 `@Validated(OnCreate.class)`
     *
     * 【为什么要换？】`@Valid` **没有 groups 属性** ——
     *   它是 Jakarta 的标准注解，能力就停在"校验默认组"。
     *   想指定"按哪一组校验"，只能用 Spring 的 `@Validated`（多了个 value() 用来传组）。
     *
     * ⚠️ 换完别忘 TODO①（SkuForm 上的 groups 也必须同时加），
     *    否则就踩上那个"**静默失效**"的坑：接口照样 200，校验一条都没跑。
     */
    @PostMapping
    public Result<Sku> add(@Validated(OnCreate.class) @RequestBody SkuForm form) {
        return Result.ok(skuService.save(form));
    }

    /**
     * ④ 更新 → PUT /sku （JSON 请求体，里面**必须带 id**）
     *
     * ⭐ TODO②：这个方法还没写，两件事：
     *   ① 参数注解：`@Valid` → `@Validated(OnUpdate.class)`
     *   ② 方法体：把下面那行 throw 删掉，改成
     *        return Result.ok(skuService.update(form));
     *      （Service 里的 `update()` 已经写好了，你只管接上）
     */
    @PutMapping
    public Result<Sku> update(@Validated(OnUpdate.class) @RequestBody SkuForm form) {
      return Result.ok(skuService.update(form));
    }

    /** ⑤ 删除 → DELETE /sku/999 （不存在 → 404；还有库存 → 409） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        skuService.delete(id);
        return Result.ok();          // 没有数据要返回，用无参 ok()（data 是 null）
    }

    /**
     * ⑥ 出库 → POST /sku/outbound?id=1&count=5
     * 三种错都能试：
     *   count=0        → 400（参数不对）
     *   id=999         → 404（东西没有）
     *   id=2&count=100 → 409（库存只有 3）
     */
    @PostMapping("/outbound")
    public Result<Sku> outbound(@RequestParam("id") Long id, @RequestParam("count") int count) {
        return Result.ok(skuService.outbound(id, count));
    }

    /**
     * ⑦ ⭐ 新增：搜索 —— **方法参数校验**的演示场（也是第 1 步要观察的那个"坑"）
     *
     * 参数上的 `@Min/@Max` 和请求体上的 `@NotBlank` **不是一回事**：
     *   · 请求体字段 → SpringMVC 在"JSON 转对象"时校验 → MethodArgumentNotValidException（Day31 接住了）
     *   · 方法参数   → 靠**类上的 @Validated** 触发 AOP 校验 → ConstraintViolationException
     *                                                        ⚠️ 现在**没人接它** → 掉进兜底 → 500
     *
     * 🎯 第 1 步请亲手试一下：`GET /sku/search?limit=0`
     *    明明是你自己传错参数，服务器却回一句"服务器开小差了" —— 这就是第 7 步要修的东西。
     */
    @GetMapping("/search")
    public Result<List<Sku>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "limit", defaultValue = "10")
            @Min(value = 1, message = "limit 至少是 1")
            @Max(value = 50, message = "limit 最多是 50") int limit) {
        return Result.ok(skuService.search(keyword, limit));
    }

    /**
     * ⑧ 故意炸 → GET /sku/boom （验证 Day31 的兜底处理器还在生效）
     * 应该返回 500 + 统一 JSON，且响应体里**看不到** NullPointerException 字样。
     */
    @GetMapping("/boom")
    public Result<Sku> boom() {
        return Result.ok(skuService.boom());
    }
}