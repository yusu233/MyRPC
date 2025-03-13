package com.pwc.server;

import com.pwc.common.MyRpcProtocol;
import io.netty.channel.ChannelHandlerContext;

public class ServerChannelReadData {
    private MyRpcProtocol rpcProtocol;
    private ChannelHandlerContext channelHandlerContext;

    public MyRpcProtocol getRpcProtocol() {
        return rpcProtocol;
    }

    public void setRpcProtocol(MyRpcProtocol rpcProtocol) {
        this.rpcProtocol = rpcProtocol;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
