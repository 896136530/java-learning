package com.xiaoming.day33.sku.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ⭐⭐ 自定义校验注解（**带参数**版） —— "库存不能超过 max 件"
 *
 * 【和 @SkuCode 的唯一区别：多了个 `max()` 方法】
 *   注解里写一个方法 = 注解就有了一个"参数"：
 *     @MaxStock                      → max 取默认值 1000
 *     @MaxStock(max = 500)           → max = 500
 *     @MaxStock(max = 50)            → max = 50
 *
 *   你可能想问：注解不是"接口"吗，接口里的方法怎么还能有默认实现？
 *   —— 注解的方法**全都是"属性"**，`default` 后面写的是"不填时用什么值"。
 *      这是 `@interface` 专用的语法（比 Java 8 的 default 方法还早）。
 *
 * 【⭐ 顺便复习 @SkuCode 的"三件套"——这里还是那三个，一个都不能少】
 *   message() / groups() / payload()，再加上 @Constraint(validatedBy = ...)。
 *
 * 【⭐⭐ 最妙的一处：{max} 占位符】
 *   message 里写的 `{max}` 会被 **Bean Validation 自动**替换成 max() 的值：
 *     本项目 SkuForm 上写的是 `@MaxStock(max = 500)`，校验失败时 fieldErrors 里就是
 *     "库存不能超过 500"（真机实测结果，见练习题文末的验收记录）
 *   → **一句 message 模板，适配所有参数值**，不用为每个上限写一条消息。
 *   （规则：`{` 里写注解的属性名，框架会调那个方法取值。）
 *   📌 进阶：想拼一段"框架猜不到"的动态消息（比如带上当前值和最大值两个数），
 *      要在校验器里用 `context.buildConstraintViolationWithTemplate("...")` 手写，
 *      见知识点"进阶"一节 —— 今天用不到。
 *
 * ⚠️ TODO④ 在 `MaxStockValidator.java` 里 —— 本文件不用改，读一遍就行。
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxStockValidator.class)
public @interface MaxStock {

    /**
     * ⭐ 注解的"参数"：库存上限。
     *    用的时候可以 `@MaxStock(max = 500)` 定制，不写就是 1000。
     */
    int max() default 1000;

    /**
     * ⭐ `{max}` 会被自动替换成上面 max() 的值（实测：max=50 时显示"库存不能超过 50"）
     */
    String message() default "库存不能超过 {max}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}