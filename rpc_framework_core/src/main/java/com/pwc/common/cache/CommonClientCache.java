package com.pwc.common.cache;

import com.pwc.common.ChannelFuturePollingRef;
import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.RpcInvocation;
import com.pwc.common.config.ClientConfig;
import com.pwc.filter.client.ClientFilterChain;
import com.pwc.router.Router;
import com.pwc.serialize.SerializeFactory;
import com.pwc.registry.URL;
import com.pwc.spi.ExtensionLoader;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue(1200); //阻塞队列
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>(); //记录发送RPC的uuid，便于对应线程处理
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();
    public static Map<String, Map<String,String>> URL_MAP = new ConcurrentHashMap<>();
    public static Set<String> SERVER_ADDRESS = new HashSet<>();
    //服务提供者
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();
    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();
    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();
    public static Router ROUTER;
    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;
    public static ClientFilterChain CLIENT_FILTER_CHAIN;
    public static ClientConfig CLIENT_CONFIG;
    public static ExtensionLoader EXTENSION_LOADER = new ExtensionLoader();
}
