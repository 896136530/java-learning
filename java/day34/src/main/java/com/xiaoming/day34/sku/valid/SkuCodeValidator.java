package com.xiaoming.day34.sku.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ⭐ @SkuCode 的"执行者" —— Day32 的**答案版**（今天不用改）
 *
 * 【ConstraintValidator<A, T> 的两个泛型】
 *   第一个 A = 你给哪个注解打工 → `SkuCode`
 *   第二个 T = 这个注解贴的字段是什么**类型** → `String`
 *
 *   ┌───────────────────────────┬──────────────────────────────────────────────┐
 *   │ initialize(A annotation)  │ 启动时**只调用一次**（读注解上写的参数）        │
 *   │                           │ 本注解没有参数，所以可以不写这个方法            │
 *   ├───────────────────────────┼──────────────────────────────────────────────┤
 *   │ isValid(T value, C ctx)   │ 每个被校验的值调用一次。true 通过 / false 报错  │
 *   └───────────────────────────┴──────────────────────────────────────────────┘
 *
 * ⭐ 铁律：**null / 空串一定要放行**（"必填"是 @NotBlank 的职责，不是我的）。
 */
public class SkuCodeValidator implements ConstraintValidator<SkuCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // ⭐ 铁律：空值放行 —— 否则 code:"" 会同时报"不能为空"和"格式不对"两条
        if (value == null || value.isBlank()) {
            return true;
        }
        // 规则：SKU- 后面正好 4 位数字
        // Java 字符串里 \ 要写两次 → \\d 才是正则的 \d
        return value.matches("SKU-\\d{4}");
    }
}