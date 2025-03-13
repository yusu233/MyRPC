package com.pwc.server;

import com.alibaba.fastjson.JSON;
import com.pwc.common.MyRpcProtocol;
import com.pwc.common.RpcInvocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

import static com.pwc.common.cache.CommonServerCache.*;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyRpcProtocol rpcMsg = (MyRpcProtocol) msg;
        ServerChannelReadData serverChannelReadData = new ServerChannelReadData();
        serverChannelReadData.setRpcProtocol(rpcMsg);
        serverChannelReadData.setChannelHandlerContext(ctx);

        SERVER_CHANNEL_DISPATCHER.add(serverChannelReadData);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if(channel.isActive()) ctx.close();
    }
}
