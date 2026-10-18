package com.xiaoming.day34.sku;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * ⭐ SKU 数据访问层（DAO / Repository）—— **今天新加的类**，把 SQL 从 Service 里拿出来
 *
 * 【本类索引：9 个方法，全是 SQL】
 *   ┌───────────────────────┬────────────────────────────────────────────────┐
 *   │ findAll()             │ SELECT ... ORDER BY id                         │
 *   │ findById(id)          │ 查一个；**查不到返回 null**（"抛 404"是 Service 的事）│
 *   │ countAll()            │ SELECT COUNT(*)                                │
 *   │ existsByCode(code)    │ 编码查重（Day32 的 409 靠它）                    │
 *   │ insert(form)          │ INSERT，并**取回数据库生成的自增 id**            │
 *   │ update(sku)           │ UPDATE title/price/stock                       │
 *   │ deleteById(id)        │ DELETE                                         │
 *   │ decreaseStock(id, n)  │ ⭐ 扣库存（下单接口会调它）                       │
 *   │ search(keyword, limit)│ LIKE + LIMIT                                   │
 *   └───────────────────────┴────────────────────────────────────────────────┘
 *
 * 【⭐ Day27 的 DAO 规矩，今天一条没变】
 *   ① **DAO 只管 SQL，不管业务**：查不到就返回 null / 空 List，**不要在这里 throw 404**
 *      （不然"不存在"到底是 404 还是"数据坏了"，调用方分不清）
 *   ② **一行数据 → 一个对象**的映射只写一次（下面的 MAPPER 常量），别在 9 个方法里抄 9 遍
 *   ③ 用 `?` 占位符 + 参数（**永远别拼字符串** → Day21 的 SQL 注入）
 *
 * 【⭐⭐ 为什么今天要把 SQL 单独放到 Repository 里？】
 *   因为今天要讲的 `@Transactional` **只管"方法边界内用到的那些连接"**。
 *   如果 SQL 散落在 Service 各处，你会很难判断"这次异常到底有没有走到回滚"。
 *   分层之后有一句很好用的话：**"事务开在 Service，SQL 收在 Repository，
 *   一次业务操作 = 在同一个事务里调了 N 个 Repository 方法。"**
 */
@Repository
public class SkuRepository {

    private final JdbcTemplate jdbc;

    public SkuRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * ⭐ 行映射：把 ResultSet 的一行变成一个 Sku 对象（Day27 的 `mapRow` 规矩）
     * `rs.getInt("stock")` 里的列名**大小写不敏感**（JDBC 规定），所以放心写小写。
     */
    private static final RowMapper<Sku> MAPPER = (rs, rowNum) -> new Sku(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("title"),
            rs.getInt("price"),
            rs.getInt("stock"));

    // ═══════════════════════ 查 ═══════════════════════

    public List<Sku> findAll() {
        return jdbc.query("SELECT id, code, title, price, stock FROM sku ORDER BY id", MAPPER);
    }

    /** 查不到返回 null —— 由 Service 决定要不要抛 404 */
    public Sku findById(Long id) {
        List<Sku> list = jdbc.query("SELECT id, code, title, price, stock FROM sku WHERE id = ?", MAPPER, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Integer countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM sku", Integer.class);
    }

    /**
     * ⭐ 编码查重（Day32 的 409「商品编码已存在」靠它）
     *
     * ⚠️ 课后思考（Day35 会还这个债）：这个"先查再插"在并发下**不安全** ——
     *    两个人同时提交同一个编码，两边都查到"不存在"，就都插进去了。
     *    真正的终极解是数据库的**唯一索引**（让数据库来拒绝），Day35 讲。
     */
    public boolean existsByCode(String code) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM sku WHERE code = ?", Integer.class, code);
        return n != null && n > 0;
    }

    public List<Sku> search(String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) {
            // 注意：LIMIT ? 也能参数化（Day28 学的），别拼字符串
            return jdbc.query("SELECT id, code, title, price, stock FROM sku ORDER BY id LIMIT ?", MAPPER, limit);
        }
        return jdbc.query("SELECT id, code, title, price, stock FROM sku WHERE title LIKE ? ORDER BY id LIMIT ?",
                MAPPER, "%" + keyword + "%", limit);
    }

    // ═══════════════════════ 写 ═══════════════════════

    /**
     * ⭐ 新增，并**取回数据库生成的自增 id**（这是今天新出现的一个 API，值得单独看）
     *
     * 【为什么要这么写？—— Day21/27 手写 JDBC 时你就问过"新插入的 id 是多少"】
     *   以前的做法：插完再 `SELECT LAST_INSERT_ID()`（多一次往返，而且并发下可能串号）。
     *   标准做法：告诉驱动"插入后把 id 还给我"：
     *     ① 第三个参数 `new String[]{"id"}` = "我要 id 这一列"
     *     ② `KeyHolder` 接住返回值（Spring 给的一个小容器）
     *     ③ `kh.getKey()` 拿出来
     *   📌 这段"匿名内部类 + lambda"看着唬人，其实就是"把 PreparedStatement 装配好交给 Spring"。
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

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM sku WHERE id = ?", id);
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
     * 返回影响行数：0 表示"没有这一行"（id 不存在）。
     */
    public int decreaseStock(Long id, int count) {
        return jdbc.update("UPDATE sku SET stock = stock - ? WHERE id = ?", count, id);
    }
}