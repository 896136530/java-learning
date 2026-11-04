package com.xiaoming.day38.mapper;

import com.xiaoming.day38.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生 Mapper 接口：**只写方法签名，不写实现**。
 *
 * <p>规矩：方法名 == mapper/StudentMapper.xml 里 &lt;select&gt; 的 id。
 * MyBatis 启动时会用动态代理给这个接口造一个实现类，你调方法 = 执行对应的 SQL。
 *
 * <p>今天要写的 5 个 TODO 都在这个接口 + 对应 XML 里（见 App 里的 TODO 输出）。
 */
public interface StudentMapper {

    // ───────── 下面 5 个是"给好的示例"，先看懂再动手 ─────────

    /** 按 id 查一个（XML 里用 resultMap 映射） */
    Student findById(@Param("id") int id);

    /** 查全部，按分数从高到低 */
    List<Student> findAll();

    /** 查全部 + 班级信息（联表 + association 自动装成 ClassInfo 对象） */
    List<Student> findAllWithClass();

    /** 按姓名模糊查（安全写法：#{}，参数值不会被当成 SQL 语法） */
    List<Student> searchByNameSafe(@Param("kw") String kw);

    /** 按姓名模糊查（危险写法：${}，字符串直接拼进 SQL —— 只用来演示注入，别在项目里用） */
    List<Student> searchByNameUnsafe(@Param("kw") String kw);

    // ───────── 下面 5 个是今天的作业，去 XML 里把 SQL 补上 ─────────

    /** TODO ①：查分数在 [min, max] 之间的学生，按分数升序 */
    List<Student> findByScore(@Param("min") int min, @Param("max") int max);

    /** TODO ②：插入一个学生，插入成功后把数据库生成的 id 回填到 student.id（useGeneratedKeys） */
    int insert(Student student);

    /** TODO ③：把某个学生的分数改成新值，返回影响行数 */
    int updateScore(@Param("id") int id, @Param("score") int score);

    /** TODO ④：按 id 查一个学生，**同时带出班级信息**（联表 + association） */
    Student findWithClass(@Param("id") int id);

    /** TODO ⑤：按 id 删一个，返回影响行数（删不存在的行返回 0，不是异常） */
    int deleteById(@Param("id") int id);
}