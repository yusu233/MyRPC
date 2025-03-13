package com.pwc.filter;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;

import java.util.List;

public interface ClientFilter extends Filter {
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
