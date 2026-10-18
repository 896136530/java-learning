package com.xiaoming.day34.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ⭐ 审计日志表（`audit_log`）的数据访问层 —— 为"传播行为"实验专门造的
 *
 * 【本类索引：3 个方法】
 *   ┌──────────────────┬───────────────────────────────────────────────────┐
 *   │ insert(action,d) │ 插一条审计记录                                     │
 *   │ findAll()        │ 查全部                                             │
 *   │ countAll()       │ COUNT(*) —— ⭐ 第 7 步就看这个数字                   │
 *   └──────────────────┴───────────────────────────────────────────────────┘
 *
 * 【⭐ 为什么要单独造一张"审计表"？】
 *   因为它天然带一个**真实业务诉求**：
 *
 *      "主业务失败要回滚，但**审计日志必须留下** —— 出了事得能查是谁、什么时候、干了什么。"
 *
 *   这两个诉求是**互相矛盾**的（一个要一起回滚、一个要单独留存），
 *   而 Spring 用**传播行为（Propagation）**来解决这种矛盾 —— 这就是第 7 步的内容。
 *
 *   ⭐ 生活类比：夫妻共同账户转账失败了要整体撤销，
 *      但"柜台的监控录像"不能因为转账失败就删掉 —— 录像要单独存一份（另开一个事务）。
 */
@Repository
public class AuditLogRepository {

    private final JdbcTemplate jdbc;

    public AuditLogRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(String action, String detail) {
        jdbc.update("INSERT INTO audit_log (action, detail, created_at) VALUES (?, ?, ?)",
                action, detail, Timestamp.valueOf(LocalDateTime.now()));
    }

    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT id, action, detail FROM audit_log ORDER BY id");
    }

    /** ⭐ 第 7 步的验收数字：审计表有几行 */
    public Integer countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM audit_log", Integer.class);
    }
}