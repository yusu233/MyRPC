package com.pwc.client;

import com.alibaba.fastjson.JSON;
import com.pwc.DataService;
import com.pwc.common.MyRpcProtocol;
import com.pwc.common.RpcDecoder;
import com.pwc.common.RpcEncoder;
import com.pwc.common.RpcInvocation;
import com.pwc.common.config.BootStrapProperties;
import com.pwc.common.config.ClientConfig;
import com.pwc.common.event.RpcListenerLoader;
import com.pwc.filter.ClientFilter;
import com.pwc.filter.client.ClientFilterChain;
import com.pwc.proxy.ProxyFactory;
import com.pwc.router.Router;
import com.pwc.serialize.SerializeFactory;
import com.pwc.common.utils.CommonUtil;
import com.pwc.registry.URL;
import com.pwc.registry.zookeeper.AbstractRegister;
import com.pwc.registry.zookeeper.ZooKeeperRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pwc.common.cache.CommonClientCache.*;
import static com.pwc.spi.ExtensionLoader.EXTENSION_LOADER_CLASS_CACHE;

public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private static EventLoopGroup clientGroup = null;
    private ClientConfig clientConfig;
    private AbstractRegister abstractRegister;
    private RpcListenerLoader rpcListenerLoader;
    private Bootstrap bootstrap = new Bootstrap();

    public RpcReference start() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new ClientHandler());
                    }
                });
        //添加监听事件处理器
        rpcListenerLoader = new RpcListenerLoader();
        rpcListenerLoader.init();
        clientConfig = BootStrapProperties.loadClientConfigFromLocal();
        CLIENT_CONFIG = clientConfig;
        this.initClientConfig();

        //代理配置
        EXTENSION_LOADER.loadExtension(ProxyFactory.class);
        LinkedHashMap<String, Class> proxyMap = EXTENSION_LOADER_CLASS_CACHE.get(ProxyFactory.class.getName());
        String proxyType = clientConfig.getProxyType();
        Class proxyClass = proxyMap.get(proxyType);
        if(proxyClass == null){
            throw new RuntimeException("no match proxyStrategy for " + proxyType);
        }
        return  new RpcReference((ProxyFactory) proxyClass.newInstance());
    }

    //客户端启动前订阅服务
    public void doSubscribeService(Class<?> serviceBean){
        if(abstractRegister == null){
            abstractRegister = new ZooKeeperRegister(clientConfig.getRegisterAddr());
        }
        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParam("host", CommonUtil.getIpAddress());
        Map<String, String> result = abstractRegister.getServiceWeightMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);
        abstractRegister.subScribe(url);
    }

    public void doConnectServer(){
        for (URL provider : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(provider.getServiceName());
            for (String providerIp : providerIps) {
                try {
                    ConnectionHandler.connect(provider.getServiceName(), providerIp);
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] connect fail ", e);
                }
            }
            URL url = new URL();
            url.addParam("servicePath",provider.getServiceName()+"/provider");
            url.addParam("providerIps", JSON.toJSONString(providerIps));
            //监听有没有新增服务
            abstractRegister.doAfterSubscribe(url);
        }
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    private void setClientConfig(ClientConfig clientConfig){
        this.clientConfig = clientConfig;
        CLIENT_CONFIG = clientConfig;
    }

    private void startClientSendMsg(){
        new Thread(new AsyncSendJob()).start();
    }

    class AsyncSendJob implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    RpcInvocation data = SEND_QUEUE.take();
                    MyRpcProtocol rpcProtocol = new MyRpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(data));
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data);
                    if(channelFuture != null){
                        channelFuture.channel().writeAndFlush(rpcProtocol);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initClientConfig() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //初始化路由策略
        EXTENSION_LOADER.loadExtension(Router.class);
        String routerStrategy = clientConfig.getRouterStrategy();
        LinkedHashMap<String, Class> routerMap = EXTENSION_LOADER_CLASS_CACHE.get(Router.class.getName());
        Class routerClass = routerMap.get(routerStrategy);
        if(routerClass == null){
            throw new RuntimeException("no match routerStrategy for " + routerStrategy);
        }
        ROUTER = (Router) routerClass.newInstance();

        //初始化序列化策略
        EXTENSION_LOADER.loadExtension(SerializeFactory.class);
        String clientSerialize = clientConfig.getClientSerialize();
        LinkedHashMap<String, Class> serializeMap = EXTENSION_LOADER_CLASS_CACHE.get(SerializeFactory.class.getName());
        Class serializeClass = serializeMap.get(clientSerialize);
        if(serializeClass == null){
            throw new RuntimeException("no match serializeStrategy for " + clientSerialize);
        }
        CLIENT_SERIALIZE_FACTORY = (SerializeFactory) serializeClass.newInstance();

        //初始化客户端过滤链
        EXTENSION_LOADER.loadExtension(ClientFilter.class);
        ClientFilterChain clientFilterChain = new ClientFilterChain();
        LinkedHashMap<String, Class> filterMap = EXTENSION_LOADER_CLASS_CACHE.get(ClientFilter.class.getName());
        Set<String> keySet = filterMap.keySet();
        for (String filterName : keySet) {
            Class filterClass = filterMap.get(filterName);
            if(filterClass == null){
                throw new RuntimeException("no match filterStrategy for " + filterName);
            }
            clientFilterChain.addClientFilter((ClientFilter) filterClass.newInstance());
        }
        CLIENT_FILTER_CHAIN = clientFilterChain;
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.start();
        RpcReferenceWrapper<DataService> dataServiceRpcReferenceWrapper = new RpcReferenceWrapper<>();
        dataServiceRpcReferenceWrapper.setAimClass(DataService.class);
        dataServiceRpcReferenceWrapper.setTimeOut(2000);
        dataServiceRpcReferenceWrapper.setAsync(false);
        dataServiceRpcReferenceWrapper.setGroup("test");
        dataServiceRpcReferenceWrapper.setServiceToken("dataService");
        dataServiceRpcReferenceWrapper.setUrl("192.168.0.108:9093");
        //代理对象
        DataService dataService = rpcReference.get(dataServiceRpcReferenceWrapper);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClientSendMsg();
        for(int i = 0; i < 100; i++){
//            Thread.sleep(1000);
            new Thread(()->{
                for (int j = 0; j < 100; j++) {
                    try {
                        String result = dataService.sendData(Thread.currentThread().getName() + " test");
                        System.out.println(result + " " + Thread.currentThread().getName());
//                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
