package com.pwc.common.event;

public interface RpcListener<T> {
    void callback(Object o);
}
