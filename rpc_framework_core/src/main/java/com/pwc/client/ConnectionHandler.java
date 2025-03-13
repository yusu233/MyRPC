package com.pwc.client;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.router.Selector;
import com.pwc.common.utils.CommonUtil;
import com.pwc.registry.URL;
import com.pwc.registry.zookeeper.ProviderNodeInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.*;

import static com.pwc.common.cache.CommonClientCache.*;


public class ConnectionHandler {
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap){
        ConnectionHandler.bootstrap = bootstrap;
    }

    /**
     * 建立连接并保存连接信息
     * @param serviceName
     * @param providerIp
     * @throws InterruptedException
     */
    public static void connect(String serviceName, String providerIp) throws InterruptedException {
        if(bootstrap == null){
            throw new RuntimeException("[ConnectionHandler] bootstrap is null");
        }

        if(!providerIp.contains(":")) return;

        String[] providerAddress = providerIp.split(":");
        String ip = providerAddress[0];
        Integer port = Integer.valueOf(providerAddress[1]);
        System.out.println(ip + ":" + port);
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        String providerURLInfo = URL_MAP.get(serviceName).get(providerIp);
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(providerURLInfo);
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setHost(ip);
        channelFutureWrapper.setPort(port);
        channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
        channelFutureWrapper.setGroup(providerNodeInfo.getGroup());
        SERVER_ADDRESS.add(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if(CommonUtil.isEmptyList(channelFutureWrappers)){
            channelFutureWrappers = new ArrayList<>();
        }
        channelFutureWrappers.add(channelFutureWrapper);
        CONNECT_MAP.put(serviceName, channelFutureWrappers);
        Selector selector = new Selector();
        selector.setProviderServiceName(serviceName);
        ROUTER.refreshRouterArr(selector);
    }

    public static ChannelFuture createChannelFuture(String ip, Integer port) throws InterruptedException {
        if(bootstrap == null){
            throw new RuntimeException("[ConnectionHandler] bootstrap is null");
        }
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        return channelFuture;
    }

    public static void unConnect(String serviceName, String providerIp){
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if(!CommonUtil.isEmptyList(channelFutureWrappers)){
            Iterator<ChannelFutureWrapper> iterator = channelFutureWrappers.iterator();
            while(iterator.hasNext()){
                ChannelFutureWrapper channelFutureWrapper = iterator.next();
                if(providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort())){
                    iterator.remove();
                }
            }
        }
    }

    public static ChannelFuture getChannelFuture(RpcInvocation rpcInvocation){
        String serviceName = rpcInvocation.getTargetServiceName();
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTER_MAP.get(serviceName);
        if (channelFutureWrappers == null || channelFutureWrappers.length == 0) {
            throw new RuntimeException("no provider left for " + serviceName);
        }
        CLIENT_FILTER_CHAIN.doFilter(new ArrayList<>(Arrays.asList(channelFutureWrappers)), rpcInvocation);
        Selector selector = new Selector();
        selector.setProviderServiceName(serviceName);
        selector.setChannelFutureWrappers(channelFutureWrappers);
        ChannelFuture channelFuture = ROUTER.select(selector).getChannelFuture();
        return channelFuture;
    }
}
