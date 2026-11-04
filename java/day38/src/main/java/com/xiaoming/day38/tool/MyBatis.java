package com.xiaoming.day38.tool;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * 一个极简的 MyBatis 启动物（等价于 Day21 的 Db 工具类）。
 *
 * <p>三行核心：
 * <pre>
 *   InputStream in = Resources.getResourceAsStream("mybatis-config.xml"); // 读配置
 *   SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in); // 建工厂（全局只建一次）
 *   SqlSession session = factory.openSession();                          // 每次操作开一个会话
 * </pre>
 *
 * <p>⚠️ SqlSession 不是线程安全的，用完必须 close（这里用 try-with-resources）。
 */
public final class MyBatis {

    /** 工厂很重（读了配置、建了连接池），全项目共用一个 */
    private static final SqlSessionFactory FACTORY = build();

    private MyBatis() {
    }

    private static SqlSessionFactory build() {
        try (InputStream in = Resources.getResourceAsStream("mybatis-config.xml")) {
            return new SqlSessionFactoryBuilder().build(in);
        } catch (Exception e) {
            throw new IllegalStateException("读不到 mybatis-config.xml，或配置有错", e);
        }
    }

    /** 开一个会话；不调 commit() 的话，关闭时自动回滚（所以演示插入不会脏数据） */
    public static SqlSession openSession() {
        return FACTORY.openSession();
    }
}