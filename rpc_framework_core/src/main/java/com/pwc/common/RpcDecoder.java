package com.pwc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.pwc.common.constants.RpcConstants.MAGIC_NUMBER;

public class RpcDecoder extends ByteToMessageDecoder {

    private static final int HEAD_LENGTH = 6; //rpc协议头部信息的固定长度
    private static final int MAX_LENGTH = 2048; //限制读取的包体的最大长度
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() >= HEAD_LENGTH){
//            if (byteBuf.readableBytes() > MAX_LENGTH){
//                System.out.println("[RpcDecoder]: MAX_LENGTH");
//                byteBuf.skipBytes(byteBuf.readableBytes());
//            }

            int startIndex = byteBuf.readerIndex();
            byteBuf.markReaderIndex();
            if(byteBuf.readShort() != MAGIC_NUMBER){
                ctx.close();
                return;
            }

            int length = byteBuf.readInt();
            //数据包不完整
            if(byteBuf.readableBytes() < length){
                byteBuf.resetReaderIndex();
                return;
            }

            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            MyRpcProtocol rpcProtocol = new MyRpcProtocol(bytes);
            list.add(rpcProtocol);
        }
    }
}
