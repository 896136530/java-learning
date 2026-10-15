package com.xiaoming.day32.sku.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ⭐ @MaxStock 的"执行者" —— TODO④ 要你写的就是这里
 *
 * 【和 @SkuCodeValidator 的对比：这里多了"读参数"这一步】
 *   @SkuCode 没有参数 → 只要实现 isValid 就够了
 *   @MaxStock 有 max   → 还要实现 initialize，把注解上的参数**读出来存好**
 *
 *   ┌──────────────────────────────┬────────────────────────────────────────────┐
 *   │ initialize(MaxStock anno)    │ **启动时只调用一次**。                    │
 *   │                              │ 参数就是"贴在被校验字段上的那个注解对象"，  │
 *   │                              │ 所以能直接读 `anno.max()`。                │
 *   ├──────────────────────────────┼────────────────────────────────────────────┤
 *   │ isValid(Integer v, C ctx)    │ 每个值调用一次。用上面存下来的 max 去比。    │
 *   └──────────────────────────────┴────────────────────────────────────────────┘
 *
 *   ⭐ 为什么非要存成字段、不直接在 isValid 里读注解？
 *     因为 isValid **每个值都调用一次**（批量导入时可能是几万次），
 *     而 max 的值**永远不会变** —— 启动时读一次就够了。
 *     这个"一次 vs 每次"的区分，是写校验器最该有的意识。
 *
 *   ⭐ 顺带一个安全问题：**校验器是单例**（Spring/Validator 只 new 一个实例，所有请求共用）。
 *     所以字段只能是**只读的配置**（像 max），
 *     ⚠️ 绝不能把"当前请求的数据"存成字段 —— 多线程下会串数据（Day18/19 讲过的并发问题）。
 *
 * 【TODO④：两件事】
 *   ① 实现 initialize：把 `annotation.max()` 存进下面的 max 字段
 *      （现在写死成 1000 了，而注解上写的是 500 —— 所以填 800 居然能过，待会儿亲手试）
 *   ② 实现 isValid：
 *      · 空值放行（和 @SkuCode 同一条铁律："必填"归 @NotNull 管）
 *      · value <= max 就通过
 */
public class MaxStockValidator implements ConstraintValidator<MaxStock, Integer> {

    /** 校验器是单例：这里只能放"配置"（注解参数），不能放请求数据 */
    private int max;

    @Override
    public void initialize(MaxStock annotation) {
        // ⭐ TODO④：改成读注解上的参数
        //   提示：形参 annotation 就是那个注解对象，它的属性方法叫 max()
        this.max = annotation.max();    // ← 占位：先写死 1000，所以注解上的 @MaxStock(max = 500) 现在**不生效**
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // ⭐ TODO④：实现校验逻辑（现在写的是"永远通过"的占位）
        //   ① 空值放行（照 @SkuCodeValidator 的铁律）
        //   ② value <= max → 通过
        if(value==null) return true;
        return value<=max;
    }
}