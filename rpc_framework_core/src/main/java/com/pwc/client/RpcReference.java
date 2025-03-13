package com.pwc.client;

import com.pwc.proxy.ProxyFactory;

import static com.pwc.common.cache.CommonClientCache.CLIENT_CONFIG;

//TODO：代理类
public class RpcReference {
    private ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable {
        setRpcReferenceWrapperConfig(rpcReferenceWrapper);
        return proxyFactory.getProxy(rpcReferenceWrapper);
    }

    private void setRpcReferenceWrapperConfig(RpcReferenceWrapper rpcReferenceWrapper){
        if (rpcReferenceWrapper.getTimeOut() == null) {
            rpcReferenceWrapper.setTimeOut(CLIENT_CONFIG.getTimeOut());
        }
    }
}
