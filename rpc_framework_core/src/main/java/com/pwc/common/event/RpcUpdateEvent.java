package com.pwc.common.event;

public class RpcUpdateEvent implements RpcEvent {
    private Object data;

    public RpcUpdateEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public RpcEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
