package com.xiaoming.day34.sku;

import com.xiaoming.day34.common.exception.BizRuleException;
import com.xiaoming.day34.common.exception.ParamException;
import com.xiaoming.day34.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SKU 业务层（Service）—— Day33 那份，**唯一的改动：数据从内存 Map 搬进了 H2 表**
 *
 * 【本类方法索引（和 Day33 一模一样，一个都没少 —— 这是"老功能不能退化"的自检点）】
 *   ┌────────────┬──────────────────────────────────────────────────────────────┐
 *   │ list()     │ 查全部                                                        │
 *   │ getById()  │ 查一个；**不存在自己抛 404**                                    │
 *   │ save()     │ 新增：手写校验 + **编码查重（409）**                             │
 *   │ update()   │ 部分更新（传了哪项就改哪项）                                     │
 *   │ delete()   │ 删除：还有库存不许删（409）                                     │
 *   │ outbound() │ 出库：400 / 404 / 409 三种错                                    │
 *   │ search()   │ 按关键字搜索 + limit 限制                                       │
 *   │ boom()     │ 故意炸一下（验证"兜底处理器"还在生效）                           │
 *   └────────────┴──────────────────────────────────────────────────────────────┘
 *
 * 【⭐ 和 Day33 的代码差异只有一句话】
 *   Day33：`private final Map<Long, Sku> db = new LinkedHashMap<>();`  ← 内存集合
 *   Day34：`private final SkuRepository repo;`                        ← 换成 SQL
 *   业务逻辑（那些 if / throw）**一行没改** —— 这就是"分层"的好处：
 *   换存储只动最下面那层，上面的判断一个字都不用碰。
 *
 * 【⭐⭐ 注意：这个类里**一个 @Transactional 都没有** —— 是故意的】
 *   今天的主角在下单那条链路（`order` 包）。SKU 这几个接口故意保持"无事务"，
 *   这样你才能对照出"有事务"和"没事务"的区别。
 *   📌 但真实项目里 `outbound()` 这种"先查再改库存"的方法**必须**加事务
 *      （不然两个人同时出库会超卖）。课后思考题会问你为什么不加它，答案在知识点第 16 节。
 */
@Service
public class SkuService {

    private final SkuRepository repo;

    public SkuService(SkuRepository repo) {
        this.repo = repo;
    }

    /** 查全部 */
    public List<Sku> list() {
        return repo.findAll();
    }

    /** 按 id 查一个 —— 找不到就 throw → 全局处理器 → 404 + 明确消息 */
    public Sku getById(Long id) {
        Sku sku = repo.findById(id);
        if (sku == null) {
            throw ResourceNotFoundException.of("SKU", id);   // ← 404
        }
        return sku;
    }

    /** 新增：两道校验（一道是自己再兜一遍，一道是注解做不到的查重） */
    public Sku save(SkuForm form) {
        // ───── 第一层：这些 @Valid 其实已经拦过一遍了（Service 兜底，别的地方直调也安全）─────
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new ParamException("书名不能为空");
        }
        if (form.getPrice() == null || form.getPrice() < 0) {
            throw new ParamException("价格不能小于 0");
        }
        if (form.getStock() == null || form.getStock() < 0) {
            throw new ParamException("库存不能小于 0");
        }

        // ───── 第二层：注解**做不到**的校验 —— 必须查数据才知道（Day32 的核心结论）─────
        if (form.getCode() != null && repo.existsByCode(form.getCode())) {
            throw new BizRuleException("商品编码已存在：" + form.getCode());   // 409
        }

        return repo.insert(form);
    }

    /** 部分更新（传了哪项就改哪项，没传的保持原样） */
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
        repo.update(sku);
        // ⭐ code 故意不给改：商品编码是"业务主键"，改了等于换了个东西
        return sku;
    }

    /** 删除（不存在 → 404；还有库存 → 409） */
    public void delete(Long id) {
        Sku sku = getById(id);
        if (sku.getStock() > 0) {
            throw new BizRuleException("该 SKU 还有 " + sku.getStock() + " 件库存，不能删除（请先清空库存）");
        }
        repo.deleteById(id);
    }

    /**
     * 出库 —— 400 / 404 / 409 三种错的活教材
     *   count <= 0   → 400（你传的参数就不对）
     *   id 不存在    → 404（东西没有）
     *   库存不够     → 409（参数没错、东西也在，但现实不允许）
     */
    public Sku outbound(Long id, int count) {
        if (count <= 0) {
            throw new ParamException("出库数量必须大于 0，你传的是 " + count);
        }
        Sku sku = getById(id);
        if (sku.getStock() < count) {
            throw new BizRuleException(
                    "库存不足：「" + sku.getTitle() + "」现有 " + sku.getStock() + " 件，你想出库 " + count + " 件");
        }
        repo.decreaseStock(id, count);
        sku.setStock(sku.getStock() - count);
        return sku;
    }

    /** 搜索：limit 的范围校验交给注解（类上的 @Validated + 参数上的 @Min/@Max） */
    public List<Sku> search(String keyword, int limit) {
        return repo.search(keyword, limit);
    }

    /** 故意炸的接口 —— 验证"兜底处理器"还在生效（Day31 的 TODO①） */
    public Sku boom() {
        Sku sku = null;
        sku.getTitle();          // ← 空指针！一行就炸
        return sku;
    }
}