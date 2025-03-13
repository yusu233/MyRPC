package com.pwc.serialize.test;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = -1728196331321496561L;
    private String name;
    private int id;
    private String info;

    public Student() {
    }

    public Student(String name, int id, String info) {
        this.name = name;
        this.id = id;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", info='" + info + '\'' +
                '}';
    }
}
