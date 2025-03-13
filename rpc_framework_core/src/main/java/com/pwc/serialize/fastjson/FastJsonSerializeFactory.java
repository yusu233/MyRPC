package com.pwc.serialize.fastjson;

import com.alibaba.fastjson.JSON;
import com.pwc.serialize.SerializeFactory;

public class FastJsonSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        String s = JSON.toJSONString(t);
        return s.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data), clazz);
    }
}
