package com.pwc.filter.server;

import com.pwc.common.RpcInvocation;
import com.pwc.filter.ServerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogFilterImpl implements ServerFilter {

    private static Logger logger = LoggerFactory.getLogger(ServerLogFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        logger.info(rpcInvocation.getAttachments().get("app_name") + " invoke ==========>" +
                rpcInvocation.getTargetServiceName() + " : " + rpcInvocation.getTargetMethod());
    }
}
