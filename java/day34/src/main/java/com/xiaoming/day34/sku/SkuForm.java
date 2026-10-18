package com.xiaoming.day34.sku;

import com.xiaoming.day34.sku.valid.MaxStock;
import com.xiaoming.day34.sku.valid.OnCreate;
import com.xiaoming.day34.sku.valid.OnUpdate;
import com.xiaoming.day34.sku.valid.SkuCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * SKU 表单对象（DTO）—— Day32 的**答案版**，今天原样沿用（一天都不改）
 *
 * 【本类索引：1 个对象 + 2 个分组 + 11 条校验注解】
 *   ┌────────┬──────────────────────────────┬──────────────────────────────┐
 *   │ 字段    │ 新增（OnCreate 组）           │ 更新（OnUpdate 组）           │
 *   ├────────┼──────────────────────────────┼──────────────────────────────┤
 *   │ id     │ @Null（不许自己传）           │ @NotNull（必须传）            │
 *   │ title  │ @NotBlank（必填）             │ @Size(min=1)（可不传）        │
 *   │ price  │ @NotNull + @Min(0)           │ @Min(0)                      │
 *   │ stock  │ @NotNull + @Min(0) + @MaxStock(500) │ @Min(0) + @MaxStock(500) │
 *   │ code   │ @NotBlank + @SkuCode         │ （无 —— 编码不许改）           │
 *   └────────┴──────────────────────────────┴──────────────────────────────┘
 *
 * ⭐ 为什么今天要留这个文件？—— 因为**拦截器和校验是"两层门"**：
 *     · 校验（Day32）：看"这次请求的数据合不合法" → 合不合法是**内容**层面的事
 *     · 拦截器（今天）：看"这次请求该不该被处理" → 该不该是**身份**层面的事
 *    真实项目里两者同时存在，而且顺序是：**Filter → 拦截器（查身份）→ 参数校验（查内容）**。
 *    ⭐ 把"身份"放在更前面是有意的：**没登录的人，连参数都不该给他验**。
 */
public class SkuForm {

    /** 新增时不许前端传 id（服务器生成；前端自己传 id 是"越权"） */
    @Null(message = "新增时不能自己指定 id（由服务器生成）", groups = OnCreate.class)
    /** 更新时必须传 id（否则不知道改谁） */
    @NotNull(message = "更新时必须指定 id", groups = OnUpdate.class)
    private Long id;

    /** @NotBlank 管新增；@Size 管更新（它对 null 放行 → "不传"没事，传了空串就报错） */
    @NotBlank(message = "书名不能为空", groups = OnCreate.class)
    @Size(min = 1, message = "书名不能是空字符串", groups = OnUpdate.class)
    private String title;

    /** @Min(0) 两边都要管 → 花括号一次挂两组 */
    @NotNull(message = "价格不能为空", groups = OnCreate.class)
    @Min(value = 0, message = "价格不能小于 0", groups = {OnCreate.class, OnUpdate.class})
    private Integer price;

    /** @MaxStock 是 Day32 写的自定义注解：库存上限 500（校验器在 valid/MaxStockValidator.java） */
    @NotNull(message = "库存不能为空", groups = OnCreate.class)
    @Min(value = 0, message = "库存不能小于 0", groups = {OnCreate.class, OnUpdate.class})
    @MaxStock(max = 500, groups = {OnCreate.class, OnUpdate.class})
    private Integer stock;

    /** 编码只在新增校验（更新不允许改编码，所以不挂 OnUpdate） */
    @NotBlank(message = "商品编码不能为空", groups = OnCreate.class)
    @SkuCode(groups = OnCreate.class)
    private String code;

    // Jackson 需要无参构造 + setter/getter 才能把 JSON 变成这个对象
    public SkuForm() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}