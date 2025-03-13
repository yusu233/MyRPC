package com.pwc.dispatcher;

import com.pwc.common.MyRpcProtocol;
import com.pwc.common.RpcInvocation;
import com.pwc.server.ServerChannelReadData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.*;

import static com.pwc.common.cache.CommonServerCache.*;

public class ServerChannelDispatcher {
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;
    private ExecutorService executorService;

    public ServerChannelDispatcher() {
    }

    public void init(int queueSize, int threadNums){
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(threadNums, threadNums, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(512));
    }

    public void add(ServerChannelReadData serverChannelReadData){
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    class ServerJobCoreHandler implements Runnable{
        private int num = 0;
        @Override
        public void run() {
            while (true){
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.submit(()->{
                        try {
                            MyRpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                            RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);
                            SERVER_FILTER_CHAIN.doFilter(rpcInvocation);
                            Object provider = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                            Method[] methods = provider.getClass().getDeclaredMethods();

                            Object result = null;
                            for (Method method : methods) {
                                if(method.getName().equals(rpcInvocation.getTargetMethod())){
                                    try {
                                        if(method.getReturnType().equals(Void.TYPE)){
                                            method.invoke(provider, rpcInvocation.getArgs());
                                        }else {
                                            result = method.invoke(provider, rpcInvocation.getArgs());
                                        }
                                        break;
                                    } catch (Exception e) {
                                        //返回服务端异常信息
                                        rpcInvocation.setE(e);
                                    }
                                }
                            }
                            rpcInvocation.setResponse(result);
                            MyRpcProtocol respProtocol = new MyRpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                            serverChannelReadData.getChannelHandlerContext().writeAndFlush(respProtocol);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startDataConsume(){
        new Thread(new ServerJobCoreHandler()).start();
    }
}
