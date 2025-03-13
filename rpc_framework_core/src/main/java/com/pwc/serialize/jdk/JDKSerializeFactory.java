package com.pwc.serialize.jdk;

import com.pwc.serialize.SerializeFactory;

import java.io.*;

public class JDKSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            oos.flush();
            oos.close();
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            return (T) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
