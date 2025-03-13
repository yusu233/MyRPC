package com.pwc.filter.client;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.pwc.common.cache.CommonClientCache.CLIENT_CONFIG;

public class ClientLogFilterImpl implements ClientFilter {

    private static Logger logger = LoggerFactory.getLogger(ClientLogFilterImpl.class);

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments().put("app_name", CLIENT_CONFIG.getApplicationName());
        logger.info(rpcInvocation.getAttachments().get("app_name") + " invoke ==========>" + rpcInvocation.getTargetServiceName());
    }
}
