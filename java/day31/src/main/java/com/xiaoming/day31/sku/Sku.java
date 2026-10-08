package com.xiaoming.day31.sku;

/**
 * SKU（库存商品）—— 今天的业务对象，用一个普通类即可
 *
 * ⭐ 今天**故意不用数据库**（Day30 的 H2 已经玩过了）。
 *    因为今天的主题是**异常处理**，数据存哪里不影响你学它 ——
 *    存内存（一个 Map）反而更干净：重启就恢复初始数据，方便你反复测各种错误。
 *
 *    📌 经验：学一个新概念时，**把无关变量降到最少**（这就是"隔离变量"的思路）。
 *       等概念懂了，再换到 MySQL 上（Day35 会做）。
 */
public class Sku {

    private Long id;
    private String title;
    private Integer price;
    private Integer stock;

    public Sku() {}

    public Sku(Long id, String title, Integer price, Integer stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    /** 出库（减少库存）—— 只改数据，**不管校验**（校验是 Service 的职责，见 SkuService） */
    public void decreaseStock(int count) {
        this.stock = this.stock - count;
    }

    // ⚠️ Day30 踩过的坑：忘了 getter → Jackson 序列化出来字段是 null
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}