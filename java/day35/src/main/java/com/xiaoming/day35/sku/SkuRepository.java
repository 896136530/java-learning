package com.xiaoming.day35.sku;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * ⭐⭐ SKU 数据访问层（DAO / Repository）—— Day34 那份 + **Day35 的逻辑删除**
 *
 * 【本类索引：9 个方法，全是 SQL】
 *   ┌───────────────────────┬────────────────────────────────────────────────┐
 *   │ findAll()             │ SELECT ... WHERE is_deleted = 0 ORDER BY id    │
 *   │ findById(id)          │ 查一个；**查不到返回 null**（"抛 404"是 Service 的事）│
 *   │ countAll()            │ SELECT COUNT(*) WHERE is_deleted = 0           │
 *   │ existsByCode(code)    │ 编码查重（Day32 的 409 靠它）                    │
 *   │ insert(form)          │ INSERT，并**取回数据库生成的自增 id**            │
 *   │ update(sku)           │ UPDATE title/price/stock                       │
 *   │ deleteById(id)        │ ⭐ TODO⑥ 现在是**物理删除**（DELETE），要改成逻辑删除 │
 *   │ decreaseStock(id, n)  │ ⭐ 扣库存（下单接口会调它）                       │
 *   │ search(keyword, limit)│ LIKE + LIMIT                                   │
 *   └───────────────────────┴────────────────────────────────────────────────┘
 *
 * 【⭐ Day27 的 DAO 规矩，今天一条没变】
 *   ① **DAO 只管 SQL，不管业务**：查不到就返回 null / 空 List，**不要在这里 throw 404**
 *   ② **一行数据 → 一个对象**的映射只写一次（下面的 MAPPER 常量）
 *   ③ 用 `?` 占位符 + 参数（**永远别拼字符串** → Day21 的 SQL 注入）
 *
 * ═══════════════════════════════════════════════════════════════════════════
 *  ⭐⭐ TODO⑥（第 8 步）：把"物理删除"改成"逻辑删除"
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 【交付版现在是什么样？】
 *   查询**全部带 `AND is_deleted = 0` 了**（下面每个 SELECT 都能看到），
 *   但 `deleteById` 还是**真删**：
 *       DELETE FROM sku WHERE id = ?
 *   → 于是出现一个非常别扭的状态：**表里有 is_deleted 这一列，却永远没人把它置成 1**。
 *     （查库看一眼就知道：`SELECT id, code, is_deleted FROM sku` 全是 0；
 *       而 `DELETE /sku/3` 之后那一行**直接消失了**，不是变成 is_deleted=1。）
 *
 * 【你要做的事：把 deleteById 改成一条 UPDATE】
 *   ┌─ 现在 ────────────────────────────────────────────────────────────────┐
 *   │ public int deleteById(Long id) {                                       │
 *   │     return jdbc.update("DELETE FROM sku WHERE id = ?", id);            │
 *   │ }                                                                      │
 *   └───────────────────────────────────────────────────────────────────────┘
 *   ┌─ 改成（思路，自己写出来）──────────────────────────────────────────────┐
 *   │ UPDATE sku SET is_deleted = 1 WHERE id = ? AND is_deleted = 0          │
 *   └───────────────────────────────────────────────────────────────────────┘
 *   ⭐ 三个细节，一个都别漏：
 *     ① `SET is_deleted = 1` —— **1 表示"已删"**（0 是"还在"）
 *     ② `AND is_deleted = 0` —— 别把已经删过的行再"删"一次（影响行数才有意义）
 *     ③ 返回**影响行数**（Day28 的规矩：0 不是异常，是"没改到东西"）
 *
 * 【⭐⭐ 为什么真实项目里几乎都用逻辑删除？—— 三个理由，面试能说】
 *   ① **数据还能找回来**：用户误删了、客服要查"这条数据以前存在过吗"，物理删除就没了
 *   ② **外键/历史数据不会断**：订单里存着 sku_id，把 sku 真删了，历史订单就指向空气了
 *   ③ **删得便宜**：DELETE 要真的挪数据页、维护索引；UPDATE 一个标记位轻得多
 *
 * 【⚠️ 逻辑删除的三个真实代价（今天全都会踩到）】
 *   ① **每个查询都必须记得带 `AND is_deleted = 0`** —— 漏一个地方就等于"删了还能查到"。
 *      真实项目里这靠 MyBatis-Plus 的 `@TableLogic` 自动加（Day38~40 会学），
 *      或者干脆把"未删除的数据"做成视图。
 *   ② **唯一索引会打架**：本表既有 `UNIQUE(code)`，又有 `UNIQUE(code, is_deleted)`。
 *      删掉 SKU-0003（is_deleted→1）之后，**再想新增一个同编码的，仍然会被
 *      `UNIQUE(code)` 拦住** —— 这是第 2 步思考题里要你亲手验证的一件事。
 *   ③ **和物理删除不是一回事**：逻辑删除后 `COUNT(*)` 和 `COUNT(*) WHERE is_deleted=0`
 *      会**给出不同的数**，写统计的时候要清楚自己用的是哪个。
 */
@Repository
public class SkuRepository {

    private final JdbcTemplate jdbc;

    public SkuRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * ⭐ 行映射：把 ResultSet 的一行变成一个 Sku 对象（Day27 的 `mapRow` 规矩）
     *
     * ⭐ Day35 的一处细节：`price` 在库里是 **DECIMAL(10,2)**，读回来是 java.math.BigDecimal；
     *   这里用 `rs.getInt("price")` —— **恰好**能读（JDBC 允许把 DECIMAL 取成 int，
     *   代价是**小数位被截掉**）。本项目的价格种子都是整数（108.00 / 79.00 / 89.00），
     *   所以看不出差别；一旦价格是 108.50，这里就会读出 108（**丢钱**）。
     *   📌 正确姿势是 `rs.getBigDecimal("price")` 并把 Sku.price 改成 BigDecimal ——
     *     那是"金额处理"的完整一课，放在以后（本包选择先在**表结构**上把类型改对）。
     *   ⭐ 这就是"类型选了 DECIMAL，但 Java 侧还没跟上"的中间状态 —— **诚实记下来**。
     */
    private static final RowMapper<Sku> MAPPER = (rs, rowNum) -> new Sku(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("title"),
            rs.getInt("price"),
            rs.getInt("stock"));

    // ═══════════════════════ 查 ═══════════════════════

    /** 查全部 —— ⭐ Day35：带上 `is_deleted = 0`，否则会把删掉的也查出来 */
    public List<Sku> findAll() {
        return jdbc.query(
                "SELECT id, code, title, price, stock FROM sku WHERE is_deleted = 0 ORDER BY id", MAPPER);
    }

    /** 查一个（查不到返回 null —— 由 Service 决定要不要抛 404） */
    public Sku findById(Long id) {
        List<Sku> list = jdbc.query(
                "SELECT id, code, title, price, stock FROM sku WHERE id = ? AND is_deleted = 0", MAPPER, id);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 数"还在的"商品 —— 逻辑删除之后这个数才是用户眼里的"一共几个商品" */
    public Integer countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM sku WHERE is_deleted = 0", Integer.class);
    }

    /**
     * ⭐⭐ 编码查重（Day32 的 409「商品编码已存在」靠它）—— **今天要还的债就在这一行**
     *
     * ⚠️⚠️ 为什么这里也要带 `is_deleted = 0`？—— 真机实测的坑，别删：
     *   如果不带，会出现"**删掉 SKU-0003 之后，再用 SKU-0003 新增 → 报 409 已存在**"，
     *   可用户明明已经把那条删掉了（他自己在列表里看不见它）。
     *   → 逻辑删除的世界里，**"存在"这个词必须先问清楚是哪种存在**：
     *       物理上存在（行还在） vs 业务上存在（is_deleted = 0）
     *     查重要的是**业务上存在**，所以必须带这个条件。
     *
     * ⚠️ 而且"先查再插"这个写法在**并发下不安全**（Day32 记下的原话）：
     *     两个人同时提交同一个编码，两边都查到"不存在"，就都插进去了。
     *     → 真正的终极解是数据库的**唯一索引**（让数据库来拒绝）——
     *       今天 sku.code 上已经有 `UNIQUE` 了，第 6 步你会亲手把这个 500 看到并处理掉。
     *
     * 📌 TODO⑤（第 6 步，和第 6 步那张"怎么验证"的表一起验收）会让你把
     *    **这一次多余的查询去掉** —— 因为唯一索引已经兜住了。
     *    现在先留着它，好让你对比"两种防线分别挡在哪儿"。
     */
    public boolean existsByCode(String code) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM sku WHERE code = ? AND is_deleted = 0", Integer.class, code);
        return n != null && n > 0;
    }

    public List<Sku> search(String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) {
            // 注意：LIMIT ? 也能参数化（Day28 学的），别拼字符串
            return jdbc.query(
                    "SELECT id, code, title, price, stock FROM sku WHERE is_deleted = 0 ORDER BY id LIMIT ?",
                    MAPPER, limit);
        }
        return jdbc.query(
                "SELECT id, code, title, price, stock FROM sku WHERE title LIKE ? AND is_deleted = 0 "
                        + "ORDER BY id LIMIT ?",
                MAPPER, "%" + keyword + "%", limit);
    }

    // ═══════════════════════ 写 ═══════════════════════

    /**
     * ⭐ 新增，并**取回数据库生成的自增 id**（Day34 学的 API，今天一行没改）
     *
     * 【为什么要这么写？】
     *   以前的做法：插完再 `SELECT LAST_INSERT_ID()`（多一次往返，并发下可能串号）。
     *   标准做法：告诉驱动"插入后把 id 还给我"：
     *     ① 第三个参数 `new String[]{"id"}` = "我要 id 这一列"
     *     ② `KeyHolder` 接住返回值
     *     ③ `kh.getKey()` 拿出来
     *   📌 注意这里**没有**写 `is_deleted` 这一列 → 靠数据库的 `DEFAULT 0` 填上。
     *     这就是"默认值"最实在的用处（第 2 步的观察点之一）。
     *
     * ⚠️⭐ 这里也是**唯一索引报错**的现场：如果 form.code 在库里已存在，
     *   `jdbc.update(...)` 会抛 `DuplicateKeyException`
     *   → 交付版没人接这个异常 → **500**（第 6 步要你亲眼看到）
     *   → TODO④ 写好 handler 之后 → **409**
     */
    public Sku insert(SkuForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO sku (code, title, price, stock) VALUES (?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, form.getCode());
            ps.setString(2, form.getTitle().trim());
            ps.setInt(3, form.getPrice());
            ps.setInt(4, form.getStock());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = (key == null) ? null : key.longValue();
        return new Sku(id, form.getCode(), form.getTitle().trim(), form.getPrice(), form.getStock());
    }

    /** 返回**影响行数**（Day28 的规矩：0 不是异常，是"没改到东西"） */
    public int update(Sku sku) {
        return jdbc.update("UPDATE sku SET title = ?, price = ?, stock = ? WHERE id = ?",
                sku.getTitle(), sku.getPrice(), sku.getStock(), sku.getId());
    }

    /**
     * ⭐⭐ TODO⑥（第 8 步）：把这条 **物理删除** 改成 **逻辑删除**
     *
     *   ┌─ 现在（交付版）──────────────────────────────────────────────────────┐
     *   │ DELETE FROM sku WHERE id = ?     ← 行真的没了，is_deleted 永远用不上    │
     *   └──────────────────────────────────────────────────────────────────────┘
     *   ┌─ 要改成 ─────────────────────────────────────────────────────────────┐
     *   │ UPDATE sku SET is_deleted = 1 WHERE id = ? AND is_deleted = 0         │
     *   └──────────────────────────────────────────────────────────────────────┘
     *
     * 验收方式（题目第 8 步给了原始期望）：
     *   ① `DELETE /sku/3`（3 号库存 0，允许删）→ 200
     *   ② `GET /db/sku`  → ⭐ **还能看到 id=3 这一行**，但 is_deleted 变成 1
     *      （交付版这里会看到**行不见了**）
     *   ③ `GET /sku`（业务列表）→ 3 条变 2 条（因为查询带了 is_deleted = 0）
     *   ④ `GET /sku/3` → 404「SKU 不存在：id=3」（业务上确实"不存在"了）
     *   ⑤ `GET /db/state` → sku 那个数组只剩 2 行（它查的也是 is_deleted = 0）
     *      ⭐ 而 `SELECT COUNT(*) FROM sku`（不带条件）**还是 3** —— 两个数不一样是**对的**
     */
    public int deleteById(Long id) {
        // ⭐ TODO⑥：把下面这一行换成 UPDATE sku SET is_deleted = 1 WHERE id = ? AND is_deleted = 0
        return jdbc.update("UPDATE sku SET is_deleted = 1 WHERE id = ? AND is_deleted = 0", id);
    }

    /**
     * ⭐ 扣库存（下单接口的核心一句）
     *
     * ⚠️ 这里的 SQL 是"减法"，所以要小心**超卖**：
     *    `UPDATE sku SET stock = stock - ? WHERE id = ?`
     *    → 两个请求同时进来，都读到 stock=3，都减 2 → 结果 -1，超卖！
     *    Day28 学过"悲观锁 / 乐观锁"，那种写法才是并发安全的。
     *    今天先不管并发（单机手动测），**专注"这条 UPDATE 会不会被回滚"**。
     *
     * ⚠️ 注意：这里**故意不写 `AND is_deleted = 0`**（Day34 原样）。
     *   严格来说应该写 —— 一个已删除的商品不该还能被下单扣库存。
     *   📌 留给你的思考题（题目文末第 5 题）：**为什么这里不加，会有什么风险？**
     *      提示：逻辑删除的"漏洞"从来不是 SQL 写错了，而是"**少写了一个条件**"。
     *
     * 返回影响行数：0 表示"没有这一行"（id 不存在）。
     */
    public int decreaseStock(Long id, int count) {
        return jdbc.update("UPDATE sku SET stock = stock - ? WHERE id = ?", count, id);
    }
}