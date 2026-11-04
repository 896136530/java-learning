package com.xiaoming.day38.entity;

/**
 * 班级实体：class_info 表的一行。
 * 今天只在联表查询里用到（老师的名字就在这张表里）。
 */
public class ClassInfo {

    private Integer id;
    private String name;
    private String teacher;

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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return String.format("ClassInfo{id=%s, name='%s', teacher='%s'}", id, name, teacher);
    }
}