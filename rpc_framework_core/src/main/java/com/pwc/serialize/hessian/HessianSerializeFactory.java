package com.pwc.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.pwc.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(byteArrayOutputStream);
            output.writeObject(t);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            Hessian2Input input = new Hessian2Input(byteArrayInputStream);
            Object object = input.readObject();
            return (T) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
