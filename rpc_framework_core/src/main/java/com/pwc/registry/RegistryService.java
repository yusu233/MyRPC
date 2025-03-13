package com.pwc.registry;

public interface RegistryService {
    /**
     * 服务端注册
     * @param url
     */
    void register(URL url);

    /**
     * 服务端下线
     * @param url
     */
    void unRegister(URL url);

    /**
     * 客户端订阅
     * @param url
     */
    void subScribe(URL url);

    /**
     * 客户端取消订阅
     * @param url
     */
    void unSubScribe(URL url);
}
