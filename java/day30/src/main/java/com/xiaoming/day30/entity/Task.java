package com.xiaoming.day30.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 学习任务（实体类 Entity）
 *
 * ⭐ 和 Day27/28 的 Book/Student 对比：
 *   Day27/28：类 + 你手写的建表 SQL + 你手写的 mapRow(ResultSet)
 *   Day30  ：只要在这个类上贴 @Entity，Hibernate 就自动帮你**建表**
 *            （字段名 → 列名自动对应），也不用写 mapRow —— 它自动把 ResultSet 变成对象
 *
 * 注意 import 是 jakarta.persistence.*（SpringBoot 3 用 jakarta，不是 javax；老教程写 javax 会报错）
 *
 * 💡 @Entity 这个类**不需要**你自己 new 来用（除了造测试数据）：
 *    数据库查出来的行，Hibernate 会自动 new 一个 Task 帮你填好字段。
 */
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // 自增主键（对应 SQL 的 AUTO_INCREMENT）
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    /** 第几天（1~5，模拟你学习计划的第几天） */
    @Column(name = "day_no", nullable = false)
    private Integer dayNo;

    /** 任务类型：Java / Python / 算法 / 项目 */
    @Column(length = 20)
    private String type;

    /** 是否完成 */
    @Column(nullable = false)
    private Boolean done = Boolean.FALSE;

    public Task() {                       // ⚠️ JPA 需要一个无参构造（Hibernate 靠它 new 对象）
    }

    public Task(String title, Integer dayNo, String type, Boolean done) {
        this.title = title;
        this.dayNo = dayNo;
        this.type = type;
        this.done = done;
    }

    // ===== getter / setter：JPA 和 Jackson 都靠它们读写字段 =====
    //       （⚠️ 不写的话，返回 JSON 时字段会是 null —— 这是新手第二个经典坑）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDayNo() {
        return dayNo;
    }

    public void setDayNo(Integer dayNo) {
        this.dayNo = dayNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}