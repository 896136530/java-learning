package com.xiaoming.day34.order;

import com.xiaoming.day34.common.exception.BizRuleException;
import com.xiaoming.day34.common.exception.ParamException;
import com.xiaoming.day34.common.exception.ResourceNotFoundException;
import com.xiaoming.day34.sku.Sku;
import com.xiaoming.day34.sku.SkuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * ⭐ 一个**"野生"的 Service** —— 故意**没有** `@Service` / `@Component`（第 8 步实验③）
 *
 * 【它的存在只有一个目的】
 *   证明"**注解写对了也没用，前提是这个类被 Spring 管**"。
 *   本类的 `place()` 上明明白白写着 `@Transactional(rollbackFor = Exception.class)`，
 *   但因为它不是 Spring Bean，**没人给它生成代理** → 注解等于一张贴在墙上的纸。
 *
 *   调用它的是 `OrderService.placeWithManualNew()`，那里是这么写的：
 *     `OrderServiceManual manual = new OrderServiceManual(orderRepository, skuRepository);`
 *                                                     ↑ 自己 new，Spring 毫不知情
 *
 * 【⭐ 一句话记住（面试可以直接说）】
 *   **"@Transactional 生效的前提是：你拿到的是 Spring 生成的那个代理对象。
 *     `new` 出来的对象是原始对象，注解再全也不会生效。"**
 *
 * 📌 顺便体会一下"自己 new 出来的对象"的另一个麻烦：
 *    它的依赖（两个 Repository）也得你**手动**传进去 —— 这就是"控制反转（IoC）"在帮你省的事。
 *    （Day30 学的 DI，今天在这里看到了"没有 DI 会怎样"。）
 */
public class OrderServiceManual {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceManual.class);

    private final OrderRepository orderRepository;
    private final SkuRepository skuRepository;

    public OrderServiceManual(OrderRepository orderRepository, SkuRepository skuRepository) {
        this.orderRepository = orderRepository;
        this.skuRepository = skuRepository;
    }

    /**
     * 和 OrderService.place 干同一件事（写订单 + 扣库存 + 抛异常）
     * ⭐ 逻辑是**故意复制**一份的：这样"没有代理"就是唯一的变量。
     */
    @Transactional(rollbackFor = Exception.class)
    public Order place(Long skuId, int count) {
        if (count <= 0) {
            throw new ParamException("下单数量必须大于 0，你传的是 " + count);
        }
        Sku sku = skuRepository.findById(skuId);
        if (sku == null) {
            throw ResourceNotFoundException.of("SKU", skuId);
        }
        if (sku.getStock() < count) {
            throw new BizRuleException("库存不足：「" + sku.getTitle() + "」现有 " + sku.getStock() + " 件");
        }

        int amount = sku.getPrice() * count;
        Order order = orderRepository.insert(sku.getCode(), sku.getTitle(), sku.getId(), count, amount);
        skuRepository.decreaseStock(skuId, count);
        log.info("[manual] （野生 Service，没有代理）已写订单 id={}，扣库存 {} 件", order.getId(), count);

        throw new BizRuleException("模拟：野生 Service 里抛异常（它的 @Transactional 没人代理，等于没写）");
    }
}