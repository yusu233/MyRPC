package com.pwc.serialize;

public interface SerializeFactory {
    /**
     * 序列化
     * @param t
     * @return
     * @param <T>
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
