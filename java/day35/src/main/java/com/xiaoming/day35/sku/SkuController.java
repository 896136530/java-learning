package com.xiaoming.day35.sku;

import com.xiaoming.day35.common.Result;
import com.xiaoming.day35.sku.valid.OnCreate;
import com.xiaoming.day35.sku.valid.OnUpdate;
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
 * SKU 接口层（Controller）—— Day32 的**答案版**（今天几乎不用改）
 *
 * 【本类索引：1 个类注解 + 8 个接口】
 *   ┌──────┬──────────────────────────────────┬─────────────────────────────────────┐
 *   │ 方法  │ 路径 / 方式                       │ 校验靠什么                           │
 *   ├──────┼──────────────────────────────────┼─────────────────────────────────────┤
 *   │ list │ GET    /sku                       │ 无                                  │
 *   │ get  │ GET    /sku/{id}                  │ 无                                  │
 *   │ add  │ POST   /sku                       │ @Validated(OnCreate.class)           │
 *   │ upd  │ PUT    /sku                       │ @Validated(OnUpdate.class)           │
 *   │ del  │ DELETE /sku/{id}                  │ 无                                  │
 *   │ out  │ POST   /sku/outbound?id=&count=   │ 无（错在 Service 里 throw）           │
 *   │ srch │ GET    /sku/search?keyword=&limit=│ 类上的 @Validated + 参数上的 @Min/@Max│
 *   │ boom │ GET    /sku/boom                  │ 无（故意炸，验证兜底）                 │
 *   └──────┴──────────────────────────────────┴─────────────────────────────────────┘
 *
 * 【⭐⭐ 类上那个 @Validated 是 Day32 的新东西，别和参数上的 @Valid 搞混】
 *   类上 @Validated → 管**方法参数**（?limit=0），靠 AOP，抛 ConstraintViolationException
 *   参数上 @Validated(组) → 管**请求体 JSON**，靠 SpringMVC，抛 MethodArgumentNotValidException
 *
 * 【⭐ 今天（Day33）要在这里想通的一件事】
 *   上面 8 个接口里，**没有一个**知道自己"该不该被处理" —— 它们只管业务。
 *   加不加登录校验、记不记日志，全都在**类外面**（拦截器/过滤器）完成。
 *   这就是"横切关注点"（cross-cutting concern）：
 *   一件**每个接口都要做、但和业务无关**的事，不该写进每个接口里。
 *   ⭐ 生活类比：小区每个单元都要门禁，但门禁不装在"每户人家家里"，装在大门口。
 */
@RestController
@RequestMapping("/sku")
@Validated      // 类级：开启"方法参数校验"（管 ?limit=0 这种，不管请求体）
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

    /** ② 查一个 → GET /sku/1 （不存在：Service 直接 throw → 404） */
    @GetMapping("/{id}")
    public Result<Sku> get(@PathVariable("id") Long id) {
        return Result.ok(skuService.getById(id));
    }

    /** ③ 新增 → POST /sku （只校验 OnCreate 组） */
    @PostMapping
    public Result<Sku> add(@Validated(OnCreate.class) @RequestBody SkuForm form) {
        return Result.ok(skuService.save(form));
    }

    /** ④ 更新 → PUT /sku （只校验 OnUpdate 组；部分更新） */
    @PutMapping
    public Result<Sku> update(@Validated(OnUpdate.class) @RequestBody SkuForm form) {
        return Result.ok(skuService.update(form));
    }

    /** ⑤ 删除 → DELETE /sku/2 （不存在 → 404；还有库存 → 409） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        skuService.delete(id);
        return Result.ok();          // 没有数据要返回，用无参 ok()（data 是 null）
    }

    /**
     * ⑥ 出库 → POST /sku/outbound?id=1&count=5
     *   count=0        → 400（参数不对）
     *   id=999         → 404（东西没有）
     *   id=2&count=100 → 409（库存只有 3）
     */
    @PostMapping("/outbound")
    public Result<Sku> outbound(@RequestParam("id") Long id, @RequestParam("count") int count) {
        return Result.ok(skuService.outbound(id, count));
    }

    /**
     * ⑦ 搜索 → GET /sku/search?keyword=Java&limit=10
     * 参数上的 @Min/@Max 靠**类上的 @Validated** 触发（AOP），失败的异常由
     * GlobalExceptionHandler ④ 接住 → 400 + fieldErrors{limit:...}
     */
    @GetMapping("/search")
    public Result<List<Sku>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "limit", defaultValue = "10")
            @Min(value = 1, message = "limit 至少是 1")
            @Max(value = 50, message = "limit 最多是 50") int limit) {
        return Result.ok(skuService.search(keyword, limit));
    }

    /** ⑧ 故意炸 → GET /sku/boom （NPE，验证兜底处理器还在生效） */
    @GetMapping("/boom")
    public Result<Sku> boom() {
        return Result.ok(skuService.boom());
    }
}