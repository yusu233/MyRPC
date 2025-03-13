package com.pwc.filter.server;

import com.pwc.common.RpcInvocation;
import com.pwc.filter.ServerFilter;
import com.pwc.common.utils.CommonUtil;
import com.pwc.server.ServiceWrapper;

import static com.pwc.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;

public class ServerTokenFilterImpl implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceToken = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        String matchToken = String.valueOf(serviceWrapper.getServiceToken());
        if(CommonUtil.isEmpty(matchToken)){
            return;
        }
        if(!CommonUtil.isEmpty(serviceToken) && serviceToken.equals(matchToken)){
            return;
        }
        throw new RuntimeException("token is " + serviceToken + " , verify result is false!");
    }
}
