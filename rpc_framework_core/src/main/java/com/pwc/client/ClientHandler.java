package com.pwc.client;

import com.alibaba.fastjson.JSON;
import com.pwc.common.MyRpcProtocol;
import com.pwc.common.RpcInvocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.pwc.common.cache.CommonClientCache.CLIENT_SERIALIZE_FACTORY;
import static com.pwc.common.cache.CommonClientCache.RESP_MAP;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyRpcProtocol rpcProtocol = (MyRpcProtocol) msg;
        byte[] content = rpcProtocol.getContent();
        RpcInvocation rpcInvocation = CLIENT_SERIALIZE_FACTORY.deserialize(content, RpcInvocation.class);
        //判断调用是否执行异常
        if(rpcInvocation.getE() != null){
            rpcInvocation.getE().printStackTrace();
        }

        Object async = rpcInvocation.getAttachments().get("async");
        if(async != null && Boolean.valueOf(String.valueOf(async))){
            ReferenceCountUtil.release(msg);
        }

        if(!RESP_MAP.containsKey(rpcInvocation.getUuid())){
            throw new IllegalArgumentException("server response is error!");
        }
        RESP_MAP.put(rpcInvocation.getUuid(), rpcInvocation);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()){
            ctx.close();
        }
    }
}
