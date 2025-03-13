package com.pwc.proxy;

import com.pwc.client.RpcReferenceWrapper;

public interface ProxyFactory {
    <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable;
}
