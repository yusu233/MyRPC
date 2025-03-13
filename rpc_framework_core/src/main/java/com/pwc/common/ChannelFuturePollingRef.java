package com.pwc.common;

import java.util.concurrent.atomic.AtomicLong;

import static com.pwc.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

public class ChannelFuturePollingRef {
    private AtomicLong referenceTimes = new AtomicLong(0);

    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName){
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTER_MAP.get(serviceName);
        long l = referenceTimes.getAndIncrement();
        return channelFutureWrappers[(int) (l % channelFutureWrappers.length)];
    }
}
