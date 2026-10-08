package com.xiaoming.day31.sku;

import com.xiaoming.day31.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ⭐ SKU 接口层（Controller）—— 注意它有多"干净"
 *
 * 对比 Day30 的 TaskController，今天这个类：
 *   · **没有一行 try-catch**
 *   · **没有一次判空**（不写 `if (x == null) return ...`）
 *   · **没有一行错误 JSON 拼装**
 * 全部交给 Service throw + GlobalExceptionHandler 收口。
 *
 * 【这带来的最大好处】
 *   以后要加一个新错误（比如"该 SKU 已被锁定"），只需要：
 *     ① 在 Service 里 throw 一个异常
 *     ② 完事 —— 状态码和 JSON 格式**自动**就对了
 *   不用去改 10 个 Controller。
 *
 * 【返回值为什么都包 Result？】
 *   统一"信封"：{ code, message, data }。成功也包，这样前端只写一套解析逻辑。
 */
@RestController
@RequestMapping("/sku")
public class SkuController {

    private final SkuService skuService;

    // ⭐ 构造器注入（Day30 用的是字段 @Autowired，两种都行；构造器注入更推荐：
    //    字段能声明成 final、不依赖 Spring 也能 new 出来测）
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
     * ⭐ 注意：**没有任何判空代码**。id=999 时 Service 会 throw，全局处理器接管 → 404
     */
    @GetMapping("/{id}")
    public Result<Sku> get(@PathVariable("id") Long id) {
        return Result.ok(skuService.getById(id));
    }

    /**
     * ③ 新增 → POST /sku （JSON 请求体）
     * @Valid 会先按 SkuForm 上的注解校验，不过就抛 MethodArgumentNotValidException
     * → 被全局处理器②接住 → 400 + fieldErrors
     */
    @PostMapping
    public Result<Sku> add(@Valid @RequestBody SkuForm form) {
        return Result.ok(skuService.save(form));
    }

    /** ④ 删除 → DELETE /sku/999 （不存在 → 404） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        skuService.delete(id);
        return Result.ok();          // 没有数据要返回，用无参 ok()（data 是 null）
    }

    /**
     * ⑤ 出库 → POST /sku/outbound?id=1&count=5
     * 三种错都能试：
     *   count=0   → 400（参数不对）
     *   id=999    → 404（东西没有）
     *   id=2&count=100 → 409（库存只有 3）
     */
    @PostMapping("/outbound")
    public Result<Sku> outbound(@RequestParam("id") Long id, @RequestParam("count") int count) {
        return Result.ok(skuService.outbound(id, count));
    }

    /**
     * ⑥ ⭐ 故意炸 → GET /sku/boom
     * 用来验证 TODO①（兜底处理）。不写兜底的话，你会看到 SpringBoot 自带的 HTML 错误页。
     */
    @GetMapping("/boom")
    public Result<Sku> boom() {
        return Result.ok(skuService.boom());
    }
}