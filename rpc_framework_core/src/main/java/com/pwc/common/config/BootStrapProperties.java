package com.pwc.common.config;


import java.io.IOException;

public class BootStrapProperties {
    private volatile boolean configIsReady;

    public static final String SERVER_PORT = "rpc.serverPort";
    public static final String REGISTER_ADDRESS = "rpc.registerAddr";
    public static final String APPLICATION_NAME = "rpc.applicationName";
    public static final String ROUTER_STRATEGY = "rpc.routerStrategy";
    public static final String SERVER_SERIALIZE_TYPE = "rpc.serverSerialize";
    public static final String CLIENT_SERIALIZE_TYPE = "rpc.clientSerialize";
    public static final String SERVER_THREAD_NUMS = "rpc.server.thread.nums";
    public static final String SERVER_QUEUE_SIZE = "rpc.server.queue.size";
    public static final String CLIENT_TIME_OUT = "rpc.client.timeout";
    public static final String SERVER_MAX_CONNECTION = "rpc.server.max.connection";
    public static final String PROXY_TYPE = "rpc.proxyType";

    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("loadServerConfigFromLocal fail,e is {}", e);
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE_TYPE));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesInteger(SERVER_QUEUE_SIZE));
        serverConfig.setServerThreadNums(PropertiesLoader.getPropertiesInteger(SERVER_THREAD_NUMS));
        serverConfig.setMaxConnections(PropertiesLoader.getPropertiesInteger(SERVER_MAX_CONNECTION));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal(){
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        clientConfig.setRegisterAddr(PropertiesLoader.getPropertiesStr(REGISTER_ADDRESS));
        clientConfig.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE_TYPE));
        clientConfig.setTimeOut(PropertiesLoader.getPropertiesInteger(CLIENT_TIME_OUT));
        clientConfig.setProxyType(PropertiesLoader.getPropertiesStr(PROXY_TYPE));
        return clientConfig;
    }
}
