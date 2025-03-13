package com.pwc.registry.zookeeper;

import com.pwc.registry.RegistryService;
import com.pwc.registry.URL;

import java.util.List;
import java.util.Map;

import static com.pwc.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static com.pwc.common.cache.CommonServerCache.PROVIDER_URL_SET;

public abstract class AbstractRegister implements RegistryService {
    @Override
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subScribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url);
    }

    @Override
    public void unSubScribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url);
    }

    public abstract void doBeforeSubscribe(URL url);
    public abstract void doAfterSubscribe(URL url);
    public abstract Map<String, String> getServiceWeightMap(String serviceName);
    public abstract List<String> getProviderIps(String serviceName);
}
