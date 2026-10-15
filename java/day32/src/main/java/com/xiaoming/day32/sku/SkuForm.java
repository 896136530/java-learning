package com.xiaoming.day32.sku;

import com.xiaoming.day32.sku.valid.MaxStock;
import com.xiaoming.day32.sku.valid.OnCreate;
import com.xiaoming.day32.sku.valid.OnUpdate;
import com.xiaoming.day32.sku.valid.SkuCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * SKU 表单对象（DTO）—— Day31 那个的升级版，**今天最核心的一个文件**
 *
 * 【今天在这里干的事：给每个注解指定"它属于哪个组"】→ 就是 TODO①
 *
 * 【为什么需要分组？一个真实的矛盾】
 *   同一个 SkuForm，新增和更新时要求**正好相反**：
 *
 *     POST /sku（新增）                   PUT /sku（更新）
 *     ───────────────────────            ───────────────────────
 *     id 必须**不传**（服务器生成）        id 必须**传**（否则不知道改谁）
 *     title 必填                          title 可不传（= 这一项不改）
 *     code 必给（格式 SKU-0001）          code 不给（编码不允许改）
 *
 *   如果只有一套注解（= Day31 的样子），你只有两条烂路：
 *     · 拆成两个 DTO 类（AddForm / UpdateForm）→ 字段全重复，改一个忘一个
 *     · 放弃注解、在 Controller 里手写 if    → 又回到 Day30 的脏代码
 *   ⭐ 分组校验是第三条路：**一套注解，按场景生效**。
 *
 * 【怎么用（3 步）】
 *   ① 两个**空接口**当"组名"：`OnCreate` / `OnUpdate`（valid 包里已经给你了）
 *   ② 注解上写 `groups = OnCreate.class`（**不写 groups 的注解属于"默认组 Default"**）
 *   ③ Controller 上写 `@Validated(OnCreate.class)` —— 只校验这一组
 *
 * 【⭐⭐ 最容易踩的坑，第 4 步会亲眼看到】
 *   只要你在 Controller 上**指定了组**，**默认组 Default 就不参与了**！
 *   于是"只改了 ③、忘了改 ②"的后果是 —— 校验**全部失效，而且一声不响**（接口照样返回 200）。
 *   这叫「**静默失效**」，比直接报错可怕十倍：报错你会去查，静默失效你以为一切正常。
 *   📌 记法：**groups 要么每组都挂上，要么一个都别写**（要写就写全）。
 */
public class SkuForm {

    /**
     * ⭐ 新增字段：id —— 同一个字段，两条**相反**的规则
     *   @Null    → 新增时不许前端传 id（服务器生成；前端自己传 id 是"越权"）
     *   @NotNull → 更新时必须传 id（否则不知道改谁）
     *
     * ⭐ TODO①：给下面两个注解分别加上 groups
     *   · @Null    → `groups = OnCreate.class`
     *   · @NotNull → `groups = OnUpdate.class`
     *   写法示例：`@Null(message = "...", groups = OnCreate.class)`
     *
     */
    // ⚠️⚠️ 这里**曾经**写成 `public interface OnCreate{}` / `OnUpdate{}`（声明在本类里）——
    //     而 Controller 上用的是 `com.xiaoming.day32.sku.valid.OnCreate`。
    //     **组名的身份是 Class 对象本身**：`SkuForm.OnCreate` 和 `valid.OnCreate` 是两个
    //     完全不同的组 → Controller 指定的组谁也匹配不上 → **注解层全部静默失效**
    //     （接口照样 200，一条约束都没跑，报错都不会报）。整天的题就死在这一行。
    //     📌 记住：组接口只该有**一份**，放在 `valid` 包里统一 import，别在本类里另起炉灶。
    //     📌 排查口诀：只要"该 400 的返回了 200"，先看 Controller 上那个组和 SkuForm 上
    //        注解里那个组**是不是同一个 Class**（IDE 里按住 Ctrl 点进去，看跳的是不是同一个文件）。

    @Null(message = "新增时不能自己指定 id（由服务器生成）",groups=OnCreate.class)
    @NotNull(message = "更新时必须指定 id",groups=OnUpdate.class)
    private Long id;

    /**
     * 书名
     *   @NotBlank         → 新增时必填（null、空串、"   " 都算不合格）
     *   @Size(min = 1)    → 更新时"可以不传，但传了就不能是空串"
     *                        （⭐ 关键：@Size 对 **null 放行**，所以"不传"不会报错）
     *
     * ⭐ TODO①：@NotBlank → `groups = OnCreate.class`；@Size → `groups = OnUpdate.class`
     */
    @NotBlank(message = "书名不能为空",groups=OnCreate.class)
    @Size(min = 1, message = "书名不能是空字符串",groups=OnUpdate.class)
    private String title;

    /**
     * 价格
     *   @NotNull → 新增时必填
     *   @Min(0)  → 不能是负数（新增、更新都要管 —— 所以它属于**两个组**）
     *
     * ⭐ TODO①：@NotNull → `groups = OnCreate.class`
     *           @Min     → `groups = {OnCreate.class, OnUpdate.class}`（**一次挂两组**，写法就是花括号）
     *   📌 顺便理解：@Min 对 null 放行 → 所以"更新时不传 price"不会触发它。
     */
    @NotNull(message = "价格不能为空",groups=OnCreate.class)
    @Min(value = 0, message = "价格不能小于 0",groups={OnCreate.class,OnUpdate.class})
    private Integer price;

    /**
     * 库存
     *   @NotNull        → 新增时必填
     *   @Min(0)         → 不能是负数
     *   @MaxStock(500)  → ⭐ **自定义注解**（带参数）：上限 **500**
     *                     校验器在 `valid/MaxStockValidator.java`，**TODO④ 要你去实现它**
     *                     ⚠️ 校验器里现在把上限**写死成 1000** 了，所以你填 800 也能过
     *                        （本该被注解上的 500 拦下）—— 这就是"TODO④ 还没写"的样子
     *
     * ⭐ TODO①：@NotNull → `groups = OnCreate.class`
     *           @Min 和 @MaxStock → `groups = {OnCreate.class, OnUpdate.class}`
     */
    @NotNull(message = "库存不能为空",groups=OnCreate.class)
    @Min(value = 0, message = "库存不能小于 0",groups={OnCreate.class,OnUpdate.class})
    @MaxStock(max = 500, groups={OnCreate.class,OnUpdate.class})
    private Integer stock;

    /**
     * 商品编码（形如 SKU-0001）
     *   @NotBlank → 新增时必填
     *   @SkuCode  → ⭐ **自定义注解**：格式必须是 "SKU-" + 4 位数字
     *               校验器在 `valid/SkuCodeValidator.java`，**TODO③ 要你去实现它**
     *               （现在它是"永远通过"的占位，所以填 abc 也能过）
     *
     * ⭐ TODO①：两个都 → `groups = OnCreate.class`（更新时不允许改编码，所以不挂 OnUpdate）
     *
     * 📌 注意 @SkuCode 和 @NotBlank 的分工 —— 这就是"每个注解只管一件事"：
     *    "有没有填" 归 @NotBlank 管，"格式对不对" 归 @SkuCode 管。
     *    所以 @SkuCode 遇到 null 必须**放行**，否则同一个字段会报两条错。
     */
    @NotBlank(message = "商品编码不能为空",groups=OnCreate.class)
    @SkuCode(groups=OnCreate.class)
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