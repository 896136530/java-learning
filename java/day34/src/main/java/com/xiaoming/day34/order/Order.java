package com.xiaoming.day34.order;

import java.time.LocalDateTime;

/**
 * 订单 —— 一张真实存在的表（`orders`）在 Java 里的样子
 *
 * 【字段和列一一对应（表在 db/DataInitializer.java 里建的）】
 *   id         → orders.id           自增主键
 *   skuId      → orders.sku_id       买的哪个商品
 *   skuCode    → orders.sku_code     ⭐ 故意冗余存一份编码/书名（真实项目常见做法：
 *   title      → orders.title           "下完单那一刻的商品快照"，商品以后改名了，
 *   count      → orders.count           历史订单里显示的还得是当时那个名字）
 *   amount     → orders.amount       ⭐ 金额 = price * count，**下单那一刻算好存下来**
 *   createdAt  → orders.created_at
 *
 * 【⭐ 今天为什么要加"订单"这个新业务？】
 *   因为事务最典型的场景就是"**一次业务操作要改两张表**"：
 *     下单 = 往 orders 插一行  +  把 sku.stock 减掉
 *   这两件事**必须同生共死** —— 只插了订单没扣库存 = 白送东西；
 *   只扣了库存没插订单 = 客户付了钱什么都没买到。
 *   Day27 你手写过"转账"（两个账户一起改），今天换成"下单"，讲的是**框架版**。
 *
 * 📌 金额今天先用 INT（延续 Day32~33 的 `Integer price`，不引入新变量）。
 *    Day35 换 MySQL 时会讲"金额必须 DECIMAL，绝不能用 float/double"。
 */
public class Order {

    private Long id;
    private Long skuId;
    private String skuCode;
    private String title;
    private Integer count;
    private Integer amount;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(Long id, Long skuId, String skuCode, String title, Integer count, Integer amount,
                 LocalDateTime createdAt) {
        this.id = id;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.title = title;
        this.count = count;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // ⚠️ Day30 踩过的坑：忘了 getter → Jackson 序列化出来字段是 null
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}