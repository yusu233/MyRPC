package com.pwc.filter.client;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.filter.ClientFilter;

import java.util.ArrayList;
import java.util.List;

public class ClientFilterChain {
    private static List<ClientFilter> clientFilters = new ArrayList<>();

    public void addClientFilter(ClientFilter clientFilter){
        clientFilters.add(clientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation){
        for (ClientFilter filter : clientFilters) {
            filter.doFilter(src, rpcInvocation);
        }
    }
}
