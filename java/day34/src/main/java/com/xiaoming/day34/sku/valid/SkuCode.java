package com.xiaoming.day34.sku.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ⭐⭐ 自定义校验注解 —— "商品编码必须是 SKU-0001 这种格式"
 *
 * 【为什么要自己写？】
 *   Bean Validation 自带的注解是"通用的"：@NotNull / @Size / @Min / @Email / @Pattern ...
 *   但业务规则是"专有的"：
 *     · 商品编码必须是 `SKU-` + 4 位数字
 *     · 手机号必须是 1 开头的 11 位
 *     · 密码必须同时含大小写和数字
 *     · 身份证号校验位必须算对
 *   这些用 @Pattern 硬凑也能凑（其实 @Pattern 就够了，真实项目也常这么干），
 *   但**当一个规则被十几处用到、或者规则本身要查库/算校验位时**，就得自己写注解。
 *   ⭐ 自定义注解真正的价值是：**把规则变成一个"能复用的词"** ——
 *      看代码的人看到 @SkuCode 就懂，不用去读一串正则。
 *
 * 【一个校验注解必须有的"三件套"】少一个就报错，这是最容易忘的地方：
 *   ① message() —— 失败时的提示语。可以写死，也能用 {属性名} 占位符（见下面 MaxStock）
 *   ② groups()  —— ⭐ 校验分组。**不声明它，这个注解永远只在默认组 Default 生效**
 *                  → 你一旦在 Controller 上指定了组（比如 OnCreate），它就被**静默跳过**！
 *   ③ payload() —— 给框架留的扩展位。几乎永远用不到，但**必须声明**（不写编译/启动就报错）
 *   再外加 `@Constraint(validatedBy = ...)` —— 指定"谁去执行这个校验"
 *
 * 【元注解（贴在注解上的注解）—— 抄一遍就懂】
 *   @Target({FIELD, PARAMETER})  这个注解能贴在哪（这里：字段、方法参数）
 *   @Retention(RUNTIME)          ⭐ 必须是 RUNTIME！
 *                                校验发生在**运行时**，要靠反射把注解读出来。
 *                                如果写成 SOURCE/CLASS，运行时读不到 → 校验静默失效。
 *   @Documented                  生成 javadoc 时把这个注解带上
 *
 * 【它和 @SkuCodeValidator 的分工】
 *   本文件  = "规则的名字 + 参数 + 范围"（声明，谁都能看懂）
 *   校验器   = "规则的实现"（代码，干活的）
 *   ⭐ 和 Day31 的"异常类型 vs 全局处理器"是同一种分工思路。
 *
 * ⚠️ Day33 的 TODO③ 在 `SkuCodeValidator.java` 里（已修好）—— 本文件不用改，读一遍就行。
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SkuCodeValidator.class)   // ← 关键：把注解和"执行者"绑起来
public @interface SkuCode {

    /** 失败时的提示语（会在 JSON 的 fieldErrors 里原样出现） */
    String message() default "商品编码格式不对（正确格式：SKU-0001）";

    /** ⭐ 分组：三件套之二。写成 {} 就是"默认组 Default" */
    Class<?>[] groups() default {};

    /** ⭐ 扩展位：三件套之三。不写它启动就会报错，但基本没人用它 */
    Class<? extends Payload>[] payload() default {};
}