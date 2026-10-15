package com.xiaoming.day32.sku.valid;

/**
 * ⭐ 校验分组 —— "新增"场景（POST /sku 用这一组）
 *
 * 【这是个什么？】
 *   一个**空接口**，连一个方法都没有。它唯一的作用就是当"标签"用。
 *
 *   你可能觉得奇怪：空接口能干嘛？
 *   —— 注解上的 `groups` 要填的是 `Class<?>[]`，也就是**类的 Class 对象**。
 *      所以要有一个"东西"能被写成 `OnCreate.class`。空接口就是最省事的那个东西。
 *
 *   📌 这个套路叫「**标记接口**」(Marker Interface)：
 *      不带任何方法，只用来"打标签"，让人或框架按标签分类。
 *      Java 自带的 `Serializable`、`Cloneable` 就是这个套路。
 *
 * 【为什么不用字符串当组名？】
 *   如果写成 `@Validated("create")`，字符串敲错一个字母**编译器不报错**（运行时才发现校验没生效）。
 *   而 `OnCreate.class` 写错 / 类被改名，**编译期直接红**。
 *   ⭐ 能用类型表达的，就别用字符串 —— 这是 Java 里反复出现的经验。
 *
 * 【为什么叫 OnCreate 而不是 Create？】
 *   惯例是 `On + 动作`（OnCreate / OnUpdate / OnDelete），
 *   读起来像"在创建的时候"，和注解 `groups = OnCreate.class` 连起来更顺。
 *
 * ⚠️ 这两个接口**已经给你了**（空接口没啥可写）。你真正要动手的是：
 *    · 在 `SkuForm` 上把注解挂到这两个组 → TODO①
 *    · 在 `SkuController` 上指定"这次校验用哪一组" → TODO②
 */
public interface OnCreate {
}