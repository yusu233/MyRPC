package com.pwc.router.rotate;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.router.Router;
import com.pwc.router.Selector;
import com.pwc.registry.URL;

import java.util.List;

import static com.pwc.common.cache.CommonClientCache.CHANNEL_FUTURE_POLLING_REF;
import static com.pwc.common.cache.CommonClientCache.CONNECT_MAP;
import static com.pwc.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * 轮询路由策略
 */
public class RotateRouterImpl implements Router {
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        int size = channelFutureWrappers.size();
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[size];
        for(int i = 0; i < size; i++){
            arr[i] = channelFutureWrappers.get(i);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getProviderServiceName());
    }

    @Override
    public void updateWeight(URL url) {}
}
