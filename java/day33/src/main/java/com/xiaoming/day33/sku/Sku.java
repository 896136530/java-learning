package com.xiaoming.day33.sku;

/**
 * SKU（库存商品）—— 业务对象，一个普通类即可
 *
 * ⭐ 和 Day31 相比只多了一个字段：`code`（商品编码，形如 SKU-0001）。
 *    加它是有目的的 —— 今天要练的"自定义校验注解"就挂在它身上
 *    （规则：必须以 SKU- 开头 + 后面 4 位数字，这种规则标准注解里没有，得自己写）。
 *
 * ⭐ 还是**故意不用数据库**（和 Day31 一样）：
 *    今天学的是"校验规则怎么定义"，跟数据存哪儿毫无关系。
 *    📌 经验：学新概念时把无关变量降到最少（隔离变量）；等概念懂了再换 MySQL（Day35）。
 */
public class Sku {

    private Long id;

    /** ⭐ 新增：商品编码（真实项目里一般是业务主键，前端拿着它查东西） */
    private String code;

    private String title;
    private Integer price;
    private Integer stock;

    public Sku() {}

    public Sku(Long id, String code, String title, Integer price, Integer stock) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    /** 出库（减少库存）—— 只改数据，**不管校验**（校验是 Service / 注解的职责） */
    public void decreaseStock(int count) {
        this.stock = this.stock - count;
    }

    // ⚠️ Day30 踩过的坑：忘了 getter → Jackson 序列化出来字段是 null
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}