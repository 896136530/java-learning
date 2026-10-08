package com.xiaoming.day30.repository;

import com.xiaoming.day30.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务仓库（Repository）
 *
 * ⭐⭐ 这是今天最"革命"的一个文件 —— 请仔细看它有多短。
 *
 * Day27/28 你写的 DAO（BookDao/StudentDao）：
 *   · 自己写 Connection、PreparedStatement
 *   · 自己写 SQL 字符串
 *   · 自己写 ResultSet → 对象的转换
 *   · 自己 try-with-resources 关资源
 *   · 5 个方法 = 大约 60 行代码
 *
 * Day30 对应的东西：
 *   interface TaskRepository extends JpaRepository<Task, Long> { }
 *   —— 一行。而且 **save / findAll / findById / deleteById / count 全都有了**。
 *
 * 谁干的活？Spring Data JPA 在启动时**动态生成这个接口的实现类**
 * （你没写实现，但运行时有实现 —— 这就是"约定 + 代理"的威力）。
 *
 * ─────── "魔法方法名"（方法名本身就是 SQL）───────
 * 下面这些方法我**没写实现**，Spring 会按**方法名**自动推导出 SQL：
 *   findByDayNo       → SELECT * FROM task WHERE day_no = ?
 *   findByDone        → SELECT * FROM task WHERE done = ?
 *   countByDone       → SELECT COUNT(*) FROM task WHERE done = ?
 *   findByTypeOrderByDayNo → SELECT * FROM task WHERE type = ? ORDER BY day_no
 *
 * 规则：findBy / countBy / deleteBy + 字段名（首字母大写）+ And/Or + OrderBy...
 * 字段名必须和实体里的**属性名**一致（是 dayNo，不是 day_no！）
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    //                            ↑实体类型    ↑主键类型

    // ===== 下面这些方法**不用你写实现**：方法名就是 SQL =====

    /** 查某一天的任务 */
    List<Task> findByDayNo(Integer dayNo);

    /** 查已完成 / 未完成的任务 */
    List<Task> findByDone(Boolean done);

    /** 数已完成的数量 */
    long countByDone(Boolean done);

    /** 按类型查，并按天排序 */
    List<Task> findByTypeOrderByDayNo(String type);

    // ===== 想写自己的 SQL 也行（Day31 会用）=====
    // @Query("SELECT t FROM Task t WHERE t.title LIKE %:kw%")
    // List<Task> searchByTitle(@Param("kw") String kw);

    /** TODO①（你写的）：查"某一天里未完成"的任务 —— 靠方法名，不写实现 */
    List<Task> findByDayNoAndDone(Integer dayNo, Boolean done);
}