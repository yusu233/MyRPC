package com.pwc.common.event.listener;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.event.RpcListener;
import com.pwc.common.event.RpcNodeChangeEvent;
import com.pwc.registry.URL;
import com.pwc.registry.zookeeper.ProviderNodeInfo;

import java.util.List;

import static com.pwc.common.cache.CommonClientCache.CONNECT_MAP;
import static com.pwc.common.cache.CommonClientCache.ROUTER;

public class ProviderNodeDataChangeListener implements RpcListener<RpcNodeChangeEvent> {
    @Override
    public void callback(Object o) {
        ProviderNodeInfo providerNodeInfo = (ProviderNodeInfo) o;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String address = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if(address.equals(providerNodeInfo.getAddress())){
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                //根据新权重更新路由策略
                ROUTER.updateWeight(url);
                break;
            }
        }
    }
}
