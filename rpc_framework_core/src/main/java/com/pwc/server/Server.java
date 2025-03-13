package com.pwc.server;

import com.pwc.common.RpcDecoder;
import com.pwc.common.RpcEncoder;
import com.pwc.common.config.BootStrapProperties;
import com.pwc.common.config.ServerConfig;
import com.pwc.common.event.RpcListenerLoader;
import com.pwc.filter.ServerFilter;
import com.pwc.filter.server.ServerFilterChain;
import com.pwc.serialize.SerializeFactory;
import com.pwc.common.utils.CommonUtil;
import com.pwc.registry.RegistryService;
import com.pwc.registry.URL;
import com.pwc.registry.zookeeper.ZooKeeperRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.pwc.common.cache.CommonClientCache.*;
import static com.pwc.common.cache.CommonServerCache.*;
import static com.pwc.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

public class Server {
    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;
    private ServerConfig serverConfig;
    private static RpcListenerLoader rpcListenerLoader;
    private RegistryService registryService;

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new MaxConnectionLimitHandler(serverConfig.getMaxConnections()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new RpcDecoder())
                                .addLast(new RpcEncoder())
                                .addLast(new ServerHandler());
                    }
                });
        this.batchExportUrl();
        SERVER_CHANNEL_DISPATCHER.startDataConsume();
        bootstrap.bind(serverConfig.getServerPort()).sync();
    }

    public void initServerConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.serverConfig = BootStrapProperties.loadServerConfigFromLocal();
        SERVER_CONFIG = serverConfig;
        SERVER_CHANNEL_DISPATCHER.init(SERVER_CONFIG.getServerQueueSize(), SERVER_CONFIG.getServerThreadNums());

        //初始化序列化策略
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String serverSerialize = serverConfig.getServerSerialize();
        LinkedHashMap<String, Class> serializeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeClass = serializeMap.get(serverSerialize);
        if(serializeClass == null){
            throw new RuntimeException("no match serializeStrategy for " + serverSerialize);
        }
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();

        //初始化服务端过滤链
        EXTENSION_LOADER.loadExtension(ServerFilter.class);
        ServerFilterChain serverFilterChain = new ServerFilterChain();
        LinkedHashMap<String, Class> filterMap = EXTENSION_LOADER_CLASS_CACHE.get(ServerFilter.class.getName());
        Set<String> keySet = filterMap.keySet();
        for (String filterName : keySet) {
            Class filterClass = filterMap.get(filterName);
            if(filterClass == null){
                throw new RuntimeException("no match filterStrategy for " + filterName);
            }
            serverFilterChain.addServerFilter((ServerFilter) filterClass.newInstance());
        }
        SERVER_FILTER_CHAIN = serverFilterChain;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * 将服务端的具体服务暴露到注册中心
     * @param serviceWrapper
     */
    public void exportService(ServiceWrapper serviceWrapper){
        Object serviceBean = serviceWrapper.getServiceObj();
        if(serviceBean.getClass().getInterfaces().length == 0){
            throw new RuntimeException("service must had interfaces!");
        }

        Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
        if (interfaces.length > 1) {
            throw new RuntimeException("service must only had one interfaces!");
        }
        if(registryService == null){
            registryService = new ZooKeeperRegister(serverConfig.getRegisterAddr());
        }

        Class<?> interfaceClass = interfaces[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
        URL url = new URL();
        url.setServiceName(interfaceClass.getName());
        url.setApplicationName(serverConfig.getApplicationName());
        String ipAddress = CommonUtil.getIpAddress();
        if(ipAddress == null){
            throw new IllegalArgumentException("ipAddress is null");
        }

        url.addParam("host", ipAddress);
        url.addParam("port", String.valueOf(serverConfig.getServerPort()));
        url.addParam("group", String.valueOf(serviceWrapper.getGroup()));
        url.addParam("limit", String.valueOf(serviceWrapper.getLimit()));
        PROVIDER_URL_SET.add(url);
        if (!CommonUtil.isEmpty(serviceWrapper.getServiceToken())) {
            PROVIDER_SERVICE_WRAPPER_MAP.put(interfaceClass.getName(), serviceWrapper);
        }
    }

    public void batchExportUrl() {
        new Thread(()->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (URL url : PROVIDER_URL_SET) {
                registryService.register(url);
            }
        }).start();
    }

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Server server = new Server();
        server.initServerConfig();
        rpcListenerLoader = new RpcListenerLoader();
        rpcListenerLoader.init();

        ServiceWrapper dataServiceWrapper = new ServiceWrapper(new DataServiceImpl(), "test");
        dataServiceWrapper.setServiceToken("dataService");
        dataServiceWrapper.setLimit(5);
        ServiceWrapper userServiceWrapper = new ServiceWrapper(new UserServiceImpl(), "test");
        userServiceWrapper.setServiceToken("userService");
        userServiceWrapper.setLimit(5);
        server.exportService(dataServiceWrapper);
        server.exportService(userServiceWrapper);
        server.start();
    }

}
