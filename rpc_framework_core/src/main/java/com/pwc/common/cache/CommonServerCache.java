package com.pwc.common.cache;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.config.ServerConfig;
import com.pwc.dispatcher.ServerChannelDispatcher;
import com.pwc.filter.server.ServerFilterChain;
import com.pwc.serialize.SerializeFactory;
import com.pwc.registry.URL;
import com.pwc.server.ServiceWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonServerCache {
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();
    //public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;
    public static ServerFilterChain SERVER_FILTER_CHAIN;
    public static ServerConfig SERVER_CONFIG;
    public static final Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();
    public static ServerChannelDispatcher SERVER_CHANNEL_DISPATCHER = new ServerChannelDispatcher();
}
