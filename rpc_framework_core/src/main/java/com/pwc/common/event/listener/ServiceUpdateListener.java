package com.pwc.common.event.listener;

import com.pwc.client.ConnectionHandler;
import com.pwc.common.ChannelFutureWrapper;
import com.pwc.common.event.RpcListener;
import com.pwc.common.event.RpcUpdateEvent;
import com.pwc.common.event.data.URLChangeWrapper;
import com.pwc.common.utils.CommonUtil;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.pwc.common.cache.CommonClientCache.CONNECT_MAP;

public class ServiceUpdateListener implements RpcListener<RpcUpdateEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUpdateListener.class);

    //更新客户端Service信息
    @Override
    public void callback(Object o) {
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) o;
        //获取节点更新前客户端存储的服务器URL
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());
        if(CommonUtil.isEmptyList(channelFutureWrappers)){
            LOGGER.error("[ServiceUpdateListener] channelFutureWrappers is empty");
            return;
        }else {
            //获取节点更新后的服务器URL
            List<String> providerUrl = urlChangeWrapper.getProviderUrl();
            HashSet<String> finalUrl = new HashSet<>();
            ArrayList<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();
            for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
                String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
                if(!providerUrl.contains(oldServerAddress)) continue;
                else {
                    finalChannelFutureWrappers.add(channelFutureWrapper);
                    finalUrl.add(oldServerAddress);
                }
            }
            ArrayList<ChannelFutureWrapper> newChannelFutureWrapper = new ArrayList<>();
            for (String s : providerUrl) {
                if(!finalUrl.contains(s)){
                    ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
                    String host = s.split(":")[0];
                    String port = s.split(":")[1];
                    try {
                        channelFutureWrapper.setHost(host);
                        channelFutureWrapper.setPort(Integer.valueOf(port));
                        ChannelFuture channelFuture = ConnectionHandler.createChannelFuture(host, Integer.valueOf(port));
                        channelFutureWrapper.setChannelFuture(channelFuture);
                        newChannelFutureWrapper.add(channelFutureWrapper);
                        finalUrl.add(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            finalChannelFutureWrappers.addAll(newChannelFutureWrapper);
            CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
        }
    }
}
