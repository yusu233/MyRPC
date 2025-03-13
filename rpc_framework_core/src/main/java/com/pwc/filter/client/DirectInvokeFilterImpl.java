package com.pwc.filter.client;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.filter.ClientFilter;
import com.pwc.common.utils.CommonUtil;

import java.util.Iterator;
import java.util.List;

public class DirectInvokeFilterImpl implements ClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = String.valueOf(rpcInvocation.getAttachments().get("url"));
        if(CommonUtil.isEmpty(url)){
            return;
        }

        Iterator<ChannelFutureWrapper> iterator = src.iterator();
        while(iterator.hasNext()){
            ChannelFutureWrapper channelFutureWrapper = iterator.next();
            if(!(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()).equals(url)){
                iterator.remove();
            }
        }
        if(CommonUtil.isEmptyList(src)){
            throw new RuntimeException("no match provider url for "+ url);
        }
    }
}
