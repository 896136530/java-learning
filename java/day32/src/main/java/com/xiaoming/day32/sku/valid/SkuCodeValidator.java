package com.xiaoming.day32.sku.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ⭐ @SkuCode 的"执行者" —— TODO③ 要你写的就是这里
 *
 * 【ConstraintValidator<A, T> 的两个泛型，别搞反】
 *   第一个 A = 你给哪个注解打工 → `SkuCode`
 *   第二个 T = 这个注解贴的字段是什么**类型** → `String`
 *   （写错了编译期就报错，所以不太容易真搞反）
 *
 * 【要实现的两个方法】
 *   ┌───────────────────────────┬──────────────────────────────────────────────┐
 *   │ initialize(A annotation)  │ **只调用一次**（启动时）。用来"读注解上写的参数"。 │
 *   │                           │ 今天这个注解**没有参数**，所以可以不写这个方法。   │
 *   │ isValid(T value, C ctx)   │ **每个被校验的值都调用一次**。                  │
 *   │                           │ 返回 true = 通过，false = 不通过（报 message）。 │
 *   └───────────────────────────┴──────────────────────────────────────────────┘
 *   ⭐ 注意职责划分：**initialize 读配置、isValid 干活** ——
 *      因为的是"注解上的参数不会变"，读一次存起来就行，别在 isValid 里反复读。
 *
 * 【⭐⭐ 最重要的一条铁律：null 一定要放行（return true）】
 *   为什么？—— "必填"是 @NotNull 的职责，不是你的职责。
 *   你要是顺手判了 null 并返回 false，就会出现**一个字段报两条错**：
 *     `{"code": "商品编码格式不对"}` 和 `{"code": "商品编码不能为空"}` 同时出现，
 *     前端在同一个输入框旁边贴两句红字，用户一脸问号。
 *   ⭐ 口诀：**每个注解只管一件事**（Single Responsibility 的老朋友）。
 *      所以在 isValid 的开头永远是这一句：
 *        if (value == null || value.isBlank()) return true;
 *
 * 【TODO③：把下面的 isValid 实现掉】
 *   ① 空值放行（照上面的铁律）
 *   ② 规则：必须以 "SKU-" 开头，后面**正好 4 位数字**（例：SKU-0001、SKU-9527）
 *      提示：String 有个方法 `matches(String regex)` 直接判断整个字符串是否匹配正则；
 *            正则在 Java 字符串里写 `\\d` 表示"一个数字"，`{4}` 表示"正好 4 个"。
 *   ③ ⚠️ 现在这行是 `return true`（永远通过）——
 *      所以待会儿你会看到：**编成 abc 这种鬼东西居然也能新增成功**。
 *      这不是 bug，是"校验器还没写"的正常状态。写完它就会变成 400。
 */
public class SkuCodeValidator implements ConstraintValidator<SkuCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // ⭐ TODO③：在这里写校验逻辑（现在写的是"永远通过"的占位）
        if (value == null || value.isBlank()) return true;
        return value.matches("SKU-\\d{4}");
    } 
}