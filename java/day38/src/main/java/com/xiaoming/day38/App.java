package com.xiaoming.day38;

import com.xiaoming.day38.entity.Student;
import com.xiaoming.day38.mapper.StudentMapper;
import com.xiaoming.day38.tool.MyBatis;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 今天的主程序：不用 Spring、不用 Tomcat，一个 main 把 MyBatis 跑通。
 *
 * <p>流程（也是面试问"MyBatis 怎么用"的标准答案）：
 * <pre>
 *   读 mybatis-config.xml → SqlSessionFactory → SqlSession
 *   → session.getMapper(StudentMapper.class) → 调方法 → 关闭会话
 * </pre>
 *
 * <p>⚠️ 注意：今天所有"写"操作都**没有 commit**（末尾直接 close），
 * 所以跑多少遍数据库都不会变 —— 这是故意的，方便你反复试。
 * 真项目里：改数据后必须 session.commit();（或者交给 Spring 的 @Transactional）。
 */
public class App {

    public static void main(String[] args) {
        System.out.println("========== Day38 MyBatis 演示 ==========");
        System.out.println("（默认不打印 SQL；想看 MyBatis 真正发的 SQL，用 运行Day38.bat 菜单 4）");
        System.out.println();

        try (SqlSession session = MyBatis.openSession()) {
            StudentMapper mapper = session.getMapper(StudentMapper.class);

            demo1_findAll(mapper);
            demo2_findById(mapper);
            demo3_safeSearch(mapper);
            demo4_injection(mapper);
            demo5_association(mapper);
            todo(mapper);
        } catch (Exception e) {
            System.out.println("❌ 整体失败：" + e.getClass().getSimpleName() + " - " + firstLine(e.getMessage()));
            System.out.println("   最常见原因：MySQL 没启动（双击 启动MySQL.bat）、库 day38 没建（菜单 1）。");
        }
    }

    private static void demo1_findAll(StudentMapper mapper) {
        section("1. findAll()：XML 里写好的 select，结果自动变成 List<Student>");
        List<Student> list = mapper.findAll();
        list.forEach(s -> System.out.println("   " + s));
        System.out.println("   共 " + list.size() + " 人");
    }

    private static void demo2_findById(StudentMapper mapper) {
        section("2. findById(3)：按 id 查一个，走 resultMap 映射");
        System.out.println("   " + mapper.findById(3));
        System.out.println("   查不到时返回：" + mapper.findById(999));
    }

    private static void demo3_safeSearch(StudentMapper mapper) {
        section("3. searchByNameSafe(\"张\")：用 #{}（占位符，安全）");
        mapper.searchByNameSafe("张").forEach(s -> System.out.println("   " + s));
    }

    private static void demo4_injection(StudentMapper mapper) {
        section("4. searchByNameUnsafe(\"' OR '1'='1\")：用 ${}（拼接，会注入）");
        String attack = "' OR '1'='1";
        List<Student> hacked = mapper.searchByNameUnsafe(attack);
        System.out.println("   传进去的是：" + attack);
        System.out.println("   返回了 " + hacked.size() + " 行（本来应该 0 行）");
        List<Student> safe = mapper.searchByNameSafe(attack);
        System.out.println("   换成 #{} 的那个方法：返回 " + safe.size() + " 行 ✅");
    }

    private static void demo5_association(StudentMapper mapper) {
        section("5. findAllWithClass()：联表 + association，自动装出 ClassInfo 对象");
        mapper.findAllWithClass().forEach(s -> System.out.println("   " + s));
    }

    /** 5 个 TODO：没写 SQL 时会报 Invalid bound statement（这是"待练习"的正常状态） */
    private static void todo(StudentMapper mapper) {
        section("6. 今天的 5 个 TODO（写好 XML 后这里会自动出结果）");

        run("TODO ① findByScore(80, 100)", () -> {
            List<Student> list = mapper.findByScore(80, 100);
            list.forEach(s -> System.out.println("      " + s));
            return "共 " + list.size() + " 人（80~100 分，升序）";
        });

        run("TODO ② insert(新同学, 77, 1)", () -> {
            Student s = new Student("测试同学", 77, 1);
            int rows = mapper.insert(s);
            return "影响行数=" + rows + "，回填的 id=" + s.getId();
        });

        run("TODO ③ updateScore(1, 99)", () -> {
            int rows = mapper.updateScore(1, 99);
            return "影响行数=" + rows + "，现在 id=1 的分数=" + mapper.findById(1).getScore();
        });

        run("TODO ④ findWithClass(2)", () -> String.valueOf(mapper.findWithClass(2)));

        run("TODO ⑤ deleteById(8)", () -> {
            int rows = mapper.deleteById(8);
            return "影响行数=" + rows + "，删不存在的 id 时返回 " + mapper.deleteById(999);
        });
    }

    // ───────── 下面是打印用的小工具，不用改 ─────────

    private interface Todo {
        String run() throws Exception;
    }

    private static void run(String title, Todo todo) {
        try {
            System.out.println("   " + title + " → " + todo.run());
        } catch (Exception e) {
            System.out.println("   " + title + " → ⏳ 还没写：" + firstLine(e.getMessage()));
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    private static String firstLine(String msg) {
        if (msg == null) {
            return "";
        }
        int i = msg.indexOf('\n');
        return i < 0 ? msg : msg.substring(0, i);
    }
}