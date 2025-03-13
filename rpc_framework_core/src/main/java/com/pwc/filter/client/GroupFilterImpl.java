package com.pwc.filter.client;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.filter.ClientFilter;
import com.pwc.common.utils.CommonUtil;

import java.util.List;

public class GroupFilterImpl implements ClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments().get("group"));
        for (ChannelFutureWrapper channelFutureWrapper : src) {
            if(!channelFutureWrapper.getGroup().equals(group)){
                src.remove(channelFutureWrapper);
            }
        }
        if(CommonUtil.isEmptyList(src)){
            throw new RuntimeException("no provider match for group " + group);
        }
    }
}
