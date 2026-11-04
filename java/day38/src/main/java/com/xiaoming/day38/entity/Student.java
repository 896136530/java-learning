package com.xiaoming.day38.entity;

import java.time.LocalDateTime;

/**
 * 学生实体：一个对象 = student 表的一行。
 *
 * <p>注意两件事：
 * <ul>
 *   <li>字段名和列名不一样（clsId ↔ cls_id）：靠 mybatis-config.xml 里的
 *       mapUnderscoreToCamelCase 自动转，或者 XML 里写 resultMap 手动映射；</li>
 *   <li>classInfo 不是表里的列：它是"这个学生属于哪个班"，靠联表查询 + association 填进来的。</li>
 * </ul>
 */
public class Student {

    private Integer id;
    private String name;
    private Integer score;
    private Integer clsId;
    private LocalDateTime createTime;

    /** 联表查出来的班级（单条查询时可能是 null） */
    private ClassInfo classInfo;

    public Student() {
    }

    public Student(String name, Integer score, Integer clsId) {
        this.name = name;
        this.score = score;
        this.clsId = clsId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getClsId() {
        return clsId;
    }

    public void setClsId(Integer clsId) {
        this.clsId = clsId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public String toString() {
        String cls = classInfo == null ? "-" : classInfo.getName() + "/" + classInfo.getTeacher();
        return String.format("Student{id=%s, name='%s', score=%s, clsId=%s, 班级=%s}",
                id, name, score, clsId, cls);
    }
}