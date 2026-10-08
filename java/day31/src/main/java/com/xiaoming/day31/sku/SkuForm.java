package com.xiaoming.day31.sku;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * SKU 表单对象（DTO）—— 专门用来接前端传来的 JSON
 *
 * ⭐ 为什么不让前端直接传 Sku 实体？
 *   ① 安全：实体里有 id、创建时间这些**不该由前端决定**的字段
 *   ② 校验：注解写在这里，就等于"接口契约"，一眼看出什么必填、范围多少
 *   ③ 解耦：以后数据库表加字段，接口不变（前端不受影响）
 *
 * ⚠️ 注意用的是 jakarta.validation（SpringBoot 3），不是 javax.validation（老教程，Day30 讲过这个坑）
 */
public class SkuForm {

    /** 书名：不能为空、不能全是空格 */
    @NotBlank(message = "书名不能为空")
    private String title;

    /** 价格：必填，且不能小于 0（@Min(0) 就是"最小值 0"） */
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于 0")
    private Integer price;

    /** 库存：必填，不能小于 0 */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于 0")
    private Integer stock;

    // Jackson 需要无参构造 + setter/getter 才能把 JSON 变成这个对象
    public SkuForm() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}