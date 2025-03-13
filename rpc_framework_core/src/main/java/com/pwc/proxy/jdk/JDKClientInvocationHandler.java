package com.pwc.proxy.jdk;

import com.pwc.client.RpcReferenceWrapper;
import com.pwc.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.pwc.common.cache.CommonClientCache.*;
import static com.pwc.common.constants.RpcConstants.DEFAULT_TIMEOUT;

//动态代理，将客户端发送的数据封装为RpcInvocation
public class JDKClientInvocationHandler implements InvocationHandler {
    private final static Object OBJECT = new Object();
    private RpcReferenceWrapper rpcReferenceWrapper;
    private int timeOut = DEFAULT_TIMEOUT;

    public JDKClientInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
        this.timeOut = Integer.parseInt(rpcReferenceWrapper.getTimeOut());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装将要传输的对象
        System.out.println(method.getName() + " " + Arrays.toString(args));
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());

        String id = UUID.randomUUID().toString();
        rpcInvocation.setUuid(id);
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttatchments());
        SEND_QUEUE.add(rpcInvocation);
        if(rpcReferenceWrapper.isAsync()) return null; //不需要服务端返回结果的请求直接
        RESP_MAP.put(id, OBJECT);
        //判断是否超时
        int cnt = 0;
        long begin = System.currentTimeMillis();
        while(System.currentTimeMillis() - begin <= timeOut || rpcInvocation.getRetry() > 0){
            Object object = RESP_MAP.get(id);
            if(object instanceof RpcInvocation){
                RESP_MAP.remove(id);
                RpcInvocation resp = (RpcInvocation) object;
                return resp.getResponse();
            }else if(rpcInvocation.getRetry() > 0 && System.currentTimeMillis() - begin > timeOut){
                //超时重试
                rpcInvocation.setRetry(rpcInvocation.getRetry() - 1);
                RESP_MAP.put(id, OBJECT);
                SEND_QUEUE.add(rpcInvocation);
                begin = System.currentTimeMillis();
            }
            Thread.sleep(50);
        }
        throw new TimeoutException(Arrays.toString(args) + " client wait server's response timeout!");
    }
}
