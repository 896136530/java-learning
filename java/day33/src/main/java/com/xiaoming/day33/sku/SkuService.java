package com.xiaoming.day33.sku;

import com.xiaoming.day33.common.exception.BizRuleException;
import com.xiaoming.day33.common.exception.ParamException;
import com.xiaoming.day33.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SKU 业务层（Service）—— Day31 那份 + 3 个新东西
 *
 * 【本类方法索引（一眼看清里面有什么）】
 *   ┌────────────┬──────────────────────────────────────────────────────────────┐
 *   │ list()     │ 查全部                                                        │
 *   │ getById()  │ 查一个；**不存在自己抛 404**（别的接口全都复用它）                │
 *   │ save()     │ 新增：手写校验 + **编码查重（409）**                             │
 *   │ update()   │ ⭐ 新增：部分更新（传了哪项就改哪项，没传的保持原样）              │
 *   │ delete()   │ 删除：还有库存不许删（409）                                     │
 *   │ outbound() │ 出库：400 / 404 / 409 三种错的活教材                            │
 *   │ search()   │ ⭐ 新增：按关键字搜索 + limit 限制（配合 @RequestParam 上的校验）  │
 *   │ boom()     │ 故意炸一下（验证"兜底处理器"还在生效）                           │
 *   └────────────┴──────────────────────────────────────────────────────────────┘
 *
 * 【⭐⭐ 今天要在这里想通的一件事：校验分三层，别混在一起】
 *   ┌────────────────┬────────────────────────────────┬──────────────────────────┐
 *   │ 层次            │ 例子                            │ 出错时状态码              │
 *   ├────────────────┼────────────────────────────────┼──────────────────────────┤
 *   │ ① 注解校验      │ code 是不是 SKU-0001 的格式      │ 400（@SkuCode 自动管）    │
 *   │  （只看自己）    │ stock 有没有超过 1000           │ 400（@MaxStock 自动管）   │
 *   ├────────────────┼────────────────────────────────┼──────────────────────────┤
 *   │ ② Service 手写  │ **编码有没有重复**              │ 409（必须查数据才知道）    │
 *   │  （要查数据）    │ 库存够不够出库                  │ 409                      │
 *   ├────────────────┼────────────────────────────────┼──────────────────────────┤
 *   │ ③ 数据库约束    │ 唯一索引、非空、外键             │ 走不到 —— 有问题就是 500   │
 *   │  （最后防线）    │                                │ （Day35 换 MySQL 后讲）   │
 *   └────────────────┴────────────────────────────────┴──────────────────────────┘
 *
 *   ⭐ 为什么"编码重复"写不成注解？—— 因为注解只能看到**当前这个对象自己的字段**，
 *      它没法去翻"别的地方有没有同款"。注解是**无状态**的，查重是**要状态**的。
 *      这个界限想明白了，你就知道什么时候该写注解、什么时候必须写 if。
 */
@Service
public class SkuService {

    /** 内存"数据库"：id → Sku（LinkedHashMap 保证遍历顺序 = 插入顺序，看着舒服） */
    private final Map<Long, Sku> db = new LinkedHashMap<>();

    /** 自增 id（AtomicLong 线程安全；今天不用纠结它，知道能自增就行） */
    private final AtomicLong idGen = new AtomicLong(0);

    public SkuService() {
        // 三条初始数据（带编码），方便你反复测各种错误
        save(form("SKU-0001", "深入理解 Java 虚拟机", 108, 10));
        save(form("SKU-0002", "Java 并发编程实战", 79, 3));
        save(form("SKU-0003", "SpringBoot 实战", 89, 0));
    }

    /** 小工具：拼一个 SkuForm（比 Day31 那种"双大括号初始化"干净些，也不容易写错） */
    private static SkuForm form(String code, String title, int price, int stock) {
        SkuForm f = new SkuForm();
        f.setCode(code);
        f.setTitle(title);
        f.setPrice(price);
        f.setStock(stock);
        return f;
    }

    // ═══════════════════════ 下面这些方法都已经写好，直接看实现 ═══════════════════════

    /** 查全部 */
    public List<Sku> list() {
        return new ArrayList<>(db.values());
    }

    /**
     * 按 id 查一个 —— ⭐ Day30 TODO③ 的正规解法
     * Day30：`orElse(null)` → 返回空白，调用方分不清"没找到"和"服务器坏了"
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
     * 新增
     */
    public Sku save(SkuForm form) {
        // ───── 第一层：这些 @Valid 其实已经拦过一遍了 ─────
        // ⚠️ 那为什么还判？—— @Valid 只管**这个 Controller 入口**。
        //    如果别的地方直接调 Service（定时任务、批量导入、单测），校验就绕过去了。
        //    **Service 的校验是最后一道防线**（Day31 讲过的理念）。
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new ParamException("书名不能为空");
        }
        if (form.getPrice() == null || form.getPrice() < 0) {
            throw new ParamException("价格不能小于 0");
        }
        if (form.getStock() == null || form.getStock() < 0) {
            throw new ParamException("库存不能小于 0");
        }

        // ───── 第二层：注解**做不到**的校验 —— 必须查数据才知道 ─────
        // ⭐ 编码查重：注解看不了"别的对象"，只能遍历 db → 只能在这里写。
        // ⭐ 状态码是 **409** 不是 400：格式没问题（参数没写错），
        //    是"和现有数据冲突了" —— 想通这一点，Day31 那张 400/404/409 的表就活了。
        for (Sku s : db.values()) {
            if (form.getCode() != null && form.getCode().equals(s.getCode())) {
                throw new BizRuleException("商品编码已存在：" + form.getCode());
            }
        }

        Sku sku = new Sku(idGen.incrementAndGet(), form.getCode(), form.getTitle().trim(),
                form.getPrice(), form.getStock());
        db.put(sku.getId(), sku);
        return sku;
    }

    /**
     * ⭐ 新增：更新 —— **部分更新**（传了哪项就改哪项，没传的保持原样）
     *
     * 【为什么要"部分"更新？】
     *   前端只想改价格时，不该被迫把书名、库存**原样再传一遍** ——
     *   万一它拿的是 5 分钟前的旧值，一回传就把别人的修改覆盖了（真实事故高发区）。
     *
     * ⚠️ 注意 `form.getId()` 这里**没有判空** —— 因为 @Validated(OnUpdate.class)
     *    上的 @NotNull 已经保证了它非空。⭐ 这就是"分组校验"帮我们省掉的一次判空，
     *    也正是"校验写在入口"的价值：**Service 里可以安心假设数据是干净的**。
     */
    public Sku update(SkuForm form) {
        Sku sku = getById(form.getId());          // 不存在 → 404（复用！）
        if (form.getTitle() != null && !form.getTitle().isBlank()) {
            sku.setTitle(form.getTitle().trim());
        }
        if (form.getPrice() != null) {
            sku.setPrice(form.getPrice());
        }
        if (form.getStock() != null) {
            sku.setStock(form.getStock());
        }
        // ⭐ code 故意不给改：商品编码是"业务主键"，改了等于换了个东西 —— 真实项目都这么定规则
        return sku;
    }

    /**
     * 删除（不存在也算"没找到" → 404；还有库存 → 409）
     */
    public void delete(Long id) {
        Sku sku = getById(id);                 // 先查 —— 不存在它会自己抛 404（不用你判空）
        if (sku.getStock() > 0) {
            throw new BizRuleException("该 SKU 还有 " + sku.getStock() + " 件库存，不能删除（请先清空库存）");
        }
        db.remove(id);
    }

    /**
     * 出库 —— ⭐ Day31 最典型的"业务规则冲突"教材
     *
     *   count <= 0   → 400（你传的参数就不对，改参数就能过）
     *   id 不存在    → 404（东西没有，改 id 就能过）
     *   库存不够     → 409（参数没错、东西也在，但**现实不允许** —— 先去进货）
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
     * ⭐ 新增：搜索 —— 专门配合"方法参数校验"（@RequestParam 上的 @Min / @Max）
     *
     * 注意这里**完全没有判 limit 的范围**（没写 `if (limit < 1)`）——
     * 交给注解了。⭐ 这就是"把参数校验从业务代码里拿出去"的样子：
     * Service 只管业务，范围检查在入口就做完了。
     */
    public List<Sku> search(String keyword, int limit) {
        List<Sku> result = new ArrayList<>();
        for (Sku s : db.values()) {
            if (keyword == null || keyword.isBlank() || s.getTitle().contains(keyword)) {
                result.add(s);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * ⭐ 故意炸的接口 —— 验证"兜底处理器"还在生效（Day31 的 TODO①）
     * 这里踩空指针（NPE），属于"没预料到的异常"，不是业务异常。
     *   没有兜底 → SpringBoot 自带的 **HTML** 错误页（前端没法解析）
     *   有兜底   → 500 + 统一 JSON
     */
    public Sku boom() {
        Sku sku = null;
        sku.getTitle();          // ← 空指针！一行就炸
        return sku;              // （上面那行已经抛异常了，这行到不了）
    }
}