package com.pwc.serialize.test;

import com.pwc.serialize.SerializeFactory;
import com.pwc.serialize.fastjson.FastJsonSerializeFactory;

public class SerializeTest {
    public static void main(String[] args) {
        SerializeFactory factory = new FastJsonSerializeFactory();
        Student student = new Student("yusu", 23, "hello world!");

        byte[] bytes = factory.serialize(student);
        System.out.println(new String(bytes));
        System.out.println("=================================");
        Student student1 = factory.deserialize(bytes, Student.class);
        System.out.println(student1);
    }
}
