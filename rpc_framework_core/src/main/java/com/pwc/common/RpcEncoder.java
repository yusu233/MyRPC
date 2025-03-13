package com.pwc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MyRpcProtocol rpcMessage = (MyRpcProtocol) o;
        byteBuf.writeShort(rpcMessage.getMagicNumber());
        byteBuf.writeInt(rpcMessage.getContentLength());
        byteBuf.writeBytes(rpcMessage.getContent());
    }
}
