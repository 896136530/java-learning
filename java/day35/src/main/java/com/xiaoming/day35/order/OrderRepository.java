package com.xiaoming.day35.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ⭐ 订单表的数据访问层（今天新加的类）
 *
 * 【本类索引：4 个方法】
 *   ┌─────────────────┬──────────────────────────────────────────────────────┐
 *   │ insert(sku, n)  │ 下单时插一行（金额在这里算：price * count）              │
 *   │ findById(id)    │ 查一个（下单成功后把订单返回给前端用）                    │
 *   │ findAll()       │ 查全部（/order/list 和 /db/orders 都用它）              │
 *   │ countAll()      │ COUNT(*) —— ⭐ 查库取证时最常数的就是这个                 │
 *   └─────────────────┴──────────────────────────────────────────────────────┘
 *
 * 【⭐⭐ 这个类里的每一行，都是"一条真实 SQL"】
 *   这就是今天选 JdbcTemplate 而不是 JPA 的原因：
 *     你看到的 `jdbc.update("INSERT INTO orders ...")` 会**立刻**发给数据库；
 *     数据库里多不多这一行，完全取决于"事务最后是 commit 还是 rollback"。
 *   换成 JPA 的 `repository.save(order)` 就没这么干脆 —— 它只是把对象挂到"持久化上下文"，
 *   **可能一条 SQL 都没发**。于是"查不到这一行"到底是因为回滚了、还是因为压根没写，
 *   你就分不清了。（知识点第 12 节把这条选型理由写全了）
 */
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 行映射：ResultSet 的一行 → 一个 Order 对象 */
    private static final RowMapper<Order> MAPPER = (rs, rowNum) -> new Order(
            rs.getLong("id"),
            rs.getLong("sku_id"),
            rs.getString("sku_code"),
            rs.getString("title"),
            rs.getInt("count"),
            rs.getInt("amount"),
            rs.getTimestamp("created_at").toLocalDateTime());

    /**
     * 下单：插一行订单，并取回数据库生成的自增 id
     *
     * ⭐ 注意这里用了 `Statement.RETURN_GENERATED_KEYS`（另一种取自增 id 的写法，
     *    和 `SkuRepository.insert` 里的 `new String[]{"id"}` 是同一个目的、两种等价写法）。
     */
    public Order insert(String skuCode, String title, Long skuId, int count, int amount) {
        LocalDateTime now = LocalDateTime.now();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders (sku_id, sku_code, title, count, amount, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, skuId);
            ps.setString(2, skuCode);
            ps.setString(3, title);
            ps.setInt(4, count);
            ps.setInt(5, amount);
            ps.setTimestamp(6, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = (key == null) ? null : key.longValue();
        return new Order(id, skuId, skuCode, title, count, amount, now);
    }

    public Order findById(Long id) {
        List<Order> list = jdbc.query(
                "SELECT id, sku_id, sku_code, title, count, amount, created_at FROM orders WHERE id = ?", MAPPER, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Order> findAll() {
        return jdbc.query(
                "SELECT id, sku_id, sku_code, title, count, amount, created_at FROM orders ORDER BY id", MAPPER);
    }

    /** ⭐ 查库取证的主角：订单表有几行 */
    public Integer countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
    }
}