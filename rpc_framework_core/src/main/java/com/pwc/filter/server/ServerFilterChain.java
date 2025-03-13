package com.pwc.filter.server;

import com.pwc.common.RpcInvocation;
import com.pwc.filter.ServerFilter;

import java.util.ArrayList;
import java.util.List;

public class ServerFilterChain {
    private static List<ServerFilter> serverFilters = new ArrayList<>();

    public void addServerFilter(ServerFilter serverFilter){
        serverFilters.add(serverFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation){
        for (ServerFilter filter : serverFilters) {
            filter.doFilter(rpcInvocation);
        }
    }
}
