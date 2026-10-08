package com.xiaoming.day31.sku;

import com.xiaoming.day31.common.exception.BizRuleException;
import com.xiaoming.day31.common.exception.ParamException;
import com.xiaoming.day31.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ⭐ SKU 业务层（Service）—— 今天的"异常产生地"
 *
 * 【Service 层是干嘛的？】
 *   Day30 你把业务逻辑全写在 Controller 里了（能跑，但不专业）。真实项目的分层是：
 *
 *     Controller  → 只管"收请求、返回响应"（不写业务规则）
 *     Service     → **业务规则 + 校验 + 事务**（今天的所有异常都在这里 throw）
 *     Repository  → 只管"存取数据"
 *
 *   ⭐ 判断标准：**"库存不够不能出库"这条规则，属于业务，不属于 HTTP** ——
 *      所以它必须在 Service，不能在 Controller。
 *      好处：以后写个定时任务/消息队列也调用这个 Service，规则不会被绕过。
 *
 * 【今天的关键设计：Service 不返回错误，而是 throw】
 *   注意看下面的方法 —— **没有一个返回 null、没有一个返回"错误码"**。
 *   遇到问题就 throw，让全局处理器统一收口。
 *   这就是"**约定优于到处判空**"。
 */
@Service
public class SkuService {

    /** 内存"数据库"：id → Sku（LinkedHashMap 保证遍历顺序 = 插入顺序，看着舒服） */
    private final Map<Long, Sku> db = new LinkedHashMap<>();

    /** 自增 id（AtomicLong 是线程安全的；今天不用纠结它，知道能自增就行） */
    private final AtomicLong idGen = new AtomicLong(0);

    public SkuService() {
        // 三条初始数据，方便你测各种错误
        save(new SkuForm() {{ setTitle("深入理解 Java 虚拟机"); setPrice(108); setStock(10); }});
        save(new SkuForm() {{ setTitle("Java 并发编程实战"); setPrice(79); setStock(3); }});
        save(new SkuForm() {{ setTitle("SpringBoot 实战"); setPrice(89); setStock(0); }});
    }

    // ═══════════════════════ 下面这些方法都已经写好，直接看实现 ═══════════════════════

    /** 查全部 */
    public List<Sku> list() {
        return new ArrayList<>(db.values());
    }

    /**
     * 按 id 查一个 —— ⭐ 这就是 Day30 TODO③ 的正规解法
     * Day30：orElse(null) → 返回空白，调用方分不清
     * Day31：找不到就 throw → 全局处理器 → 404 + 明确消息
     */
    public Sku getById(Long id) {
        Sku sku = db.get(id);
        if (sku == null) {
            throw ResourceNotFoundException.of("SKU", id);   // ← 404
        }
        return sku;
    }

    /**
     * 新增 —— 参数校验失败时 throw
     * ⚠️ 注意：@Valid 已经拦掉了"空书名/负价格"，那这里为什么还要判？
     *    因为 @Valid 只管**这个接口**；如果别的地方（定时任务/导入）调用 Service，
     *    校验就绕过去了。**Service 的校验是最后一道防线**（重要理念）。
     */
    public Sku save(SkuForm form) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new ParamException("书名不能为空");
        }
        if (form.getPrice() == null || form.getPrice() < 0) {
            throw new ParamException("价格不能小于 0");
        }
        if (form.getStock() == null || form.getStock() < 0) {
            throw new ParamException("库存不能小于 0");
        }
        Sku sku = new Sku(idGen.incrementAndGet(), form.getTitle().trim(), form.getPrice(), form.getStock());
        db.put(sku.getId(), sku);
        return sku;
    }

    /** 删除（不存在也算"没找到"） */
    public void delete(Long id) {
        if (db.remove(id) == null) {
            throw ResourceNotFoundException.of("SKU", id);   // ← 404
        }
    }

    /**
     * 出库 —— ⭐ **今天最典型的"业务规则冲突"**
     *
     * 三种错误的区别，看这个方法的写法就懂了：
     *   count <= 0        → 400（你传的参数就不对，改参数就能过）
     *   id 不存在         → 404（东西没有，改 id 就能过）
     *   库存不够          → 409（参数没错、东西也在，但**现实不允许** ——
     *                          你得先去进货，否则重试一万次也还是失败）
     */
    public Sku outbound(Long id, int count) {
        if (count <= 0) {
            throw new ParamException("出库数量必须大于 0，你传的是 " + count);
        }
        Sku sku = getById(id);                    // ← 不存在会在这里抛 404（复用！）
        if (sku.getStock() < count) {
            throw new BizRuleException(
                    "库存不足：「" + sku.getTitle() + "」现有 " + sku.getStock() + " 件，你想出库 " + count + " 件");
        }
        sku.decreaseStock(count);
        return sku;
    }

    /**
     * ⭐ 故意炸的接口 —— 用来验证你的 TODO①（兜底处理）
     * 这里会踩空指针（NPE），属于"没预料到的异常"（不是业务异常）。
     *   没有兜底处理器 → SpringBoot 返回自带的**HTML** 错误页（前端没法解析）
     *   有了兜底处理器 → 500 + 统一 JSON
     */
    public Sku boom() {
        Sku sku = null;
        sku.getTitle();          // ← 空指针！一行代码就炸
        return sku;              // （上面那行已经抛异常了，这行到不了）
    }
}