package com.pwc.common.event;

import com.pwc.common.event.listener.ProviderNodeDataChangeListener;
import com.pwc.common.event.listener.ServiceUpdateListener;
import com.pwc.common.utils.CommonUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcListenerLoader {
    private static List<RpcListener> rpcListenerList = new ArrayList<>();
    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);

    public static void registerListener(RpcListener rpcListener){
        rpcListenerList.add(rpcListener);
    }

    public void init(){
        registerListener(new ServiceUpdateListener());
        registerListener(new ProviderNodeDataChangeListener());
    }

    public static Class<?> getInterfaceT(Object o){
        Type[] interfaces = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) interfaces[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if(type instanceof Class<?>){
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(RpcEvent rpcEvent){
        if(CommonUtil.isEmptyList(rpcListenerList)) return;

        for (RpcListener<?> rpcListener : rpcListenerList) {
            Class<?> type = getInterfaceT(rpcListener);
            if(type.equals(rpcEvent.getClass())){
                eventThreadPool.execute(()->{
                    try {
                        rpcListener.callback(rpcEvent.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
