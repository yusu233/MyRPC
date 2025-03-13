package com.pwc.filter;

import com.pwc.common.RpcInvocation;

public interface ServerFilter extends Filter {
    void doFilter(RpcInvocation rpcInvocation);
}
