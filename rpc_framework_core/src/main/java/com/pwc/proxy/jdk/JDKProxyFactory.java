package com.pwc.proxy.jdk;

import com.pwc.client.RpcReferenceWrapper;
import com.pwc.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable {
        return (T) Proxy.newProxyInstance(rpcReferenceWrapper.getAimClass().getClassLoader(),
                    new Class[]{rpcReferenceWrapper.getAimClass()}, new JDKClientInvocationHandler(rpcReferenceWrapper));
    }
}
