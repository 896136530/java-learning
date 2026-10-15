package com.xiaoming.day33.sku.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ⭐ @MaxStock 的"执行者" —— Day32 的**答案版**（今天不用改）
 *
 * 【和 SkuCodeValidator 的对比：这里多了"读参数"这一步】
 *   @SkuCode 没有参数 → 只实现 isValid 就够
 *   @MaxStock 有 max   → 还要实现 initialize，把注解上的参数读出来存好
 *
 *   ⭐ 为什么不直接在 isValid 里读注解？—— isValid **每个值都调用一次**，
 *      而 max 的值**永远不会变**，启动时读一次就够（批量导入几万条时的性能差别）。
 *
 * ⭐ 顺带一个安全问题：**校验器是单例**（全程只 new 一个实例，所有请求共用）。
 *    所以字段只能是**只读的配置**（像 max），绝不能把"当前请求的数据"存成字段。
 *    📌 今天要学的 ThreadLocal，正好是"单例里怎么存一次请求的数据"的答案（知识点第 10 节）。
 */
public class MaxStockValidator implements ConstraintValidator<MaxStock, Integer> {

    /** 只放"配置"（注解参数），绝不放"请求数据" —— 校验器是单例，多线程共用 */
    private int max;

    @Override
    public void initialize(MaxStock annotation) {
        // 形参就是"贴在该字段上的那个注解对象"，从它身上把参数读出来
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // ⭐ 铁律：空值放行（"必填"归 @NotNull）
        if (value == null) {
            return true;
        }
        // 注意是 <=：写成 < 的话，正好等于上限（500）会被判错
        return value <= max;
    }
}