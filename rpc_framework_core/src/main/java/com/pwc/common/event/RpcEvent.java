package com.pwc.common.event;

public interface RpcEvent {
    Object getData();
    RpcEvent setData(Object data);
}
