package com.pwc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.pwc.common.event.RpcEvent;
import com.pwc.common.event.RpcListenerLoader;
import com.pwc.common.event.RpcNodeChangeEvent;
import com.pwc.common.event.RpcUpdateEvent;
import com.pwc.common.event.data.URLChangeWrapper;
import com.pwc.registry.URL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZooKeeperRegister extends AbstractRegister {
    private AbstractZooKeeperClient zkClient;
    private String ROOT = "/rpc";

    public ZooKeeperRegister(String address){
        this.zkClient = new CuratorZooKeeperClient(address);
    }

    public AbstractZooKeeperClient getZkClient() {
        return zkClient;
    }

    private String getProviderPath(URL url){
        return ROOT + "/" +
                url.getServiceName() + "/provider/" +
                url.getParams().get("host") + ":" +
                url.getParams().get("port");
    }
    private String getConsumerPath(URL url){
        return ROOT + "/" +
                url.getServiceName() + "/consumer/" +
                url.getApplicationName() + ":" +
                url.getParams().get("host") + ":";
    }

    @Override
    public void register(URL url) {
        if(!zkClient.existNode(ROOT)){
            //创建永久的根节点
            zkClient.createPersistentData(ROOT, "");
        }
        String urlStr = URL.generateProviderStr(url);
        System.out.println("urlStr = " + urlStr);
        String providerPath = getProviderPath(url);
        System.out.println("providerPath = " + providerPath);
        if(!zkClient.existNode(providerPath)){
            zkClient.createTemporaryData(providerPath, urlStr);
        }else{
           zkClient.deleteNode(providerPath);
           zkClient.createTemporaryData(providerPath, urlStr);
        }
        super.register(url);
    }

    @Override
    public void unRegister(URL url) {
        zkClient.deleteNode(getProviderPath(url));
        super.unRegister(url);
    }

    @Override
    public void subScribe(URL url) {
        if(!zkClient.existNode(ROOT)){
            zkClient.createPersistentData(ROOT, "");
        }

        String urlStr = URL.generateConsumerStr(url);
        String consumerPath = getConsumerPath(url);

        if(!zkClient.existNode(consumerPath)){
            zkClient.createTemporaryData(consumerPath, urlStr);
        }else {
            zkClient.deleteNode(consumerPath);
            zkClient.createTemporaryData(consumerPath, urlStr);
        }
        super.subScribe(url);
    }

    @Override
    public void unSubScribe(URL url) {
        zkClient.deleteNode(getConsumerPath(url));
        super.unSubScribe(url);
    }

    @Override
    public void doBeforeSubscribe(URL url) {

    }

    @Override
    public void doAfterSubscribe(URL url) {
        String servicePath = url.getParams().get("servicePath");
        String newServerNodePath = ROOT + "/" + servicePath;
        watchChildNodeData(newServerNodePath);
        String providerIpsJSON = url.getParams().get("providerIps");
        List<String> providerIpList = JSON.parseObject(providerIpsJSON, List.class);
        for (String ip : providerIpList) {
            this.watchNodeDataChange(ROOT + "/" + servicePath + "/" + ip);
        }
    }

    @Override
    public Map<String, String> getServiceWeightMap(String serviceName) {
        List<String> nodeDataList = this.zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
        Map<String, String> result = new HashMap<>();
        for (String ipAndHost : nodeDataList) {
            String childData = this.zkClient.getNodeData(ROOT + "/" + serviceName + "/provider/" + ipAndHost);
            result.put(ipAndHost, childData);
        }
        return result;
    }

    //节点数据修改监控
    private void watchNodeDataChange(String serverNodePath) {
        zkClient.watchNodeData(serverNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                String path = watchedEvent.getPath();
                String nodeData = zkClient.getNodeData(path);
                nodeData = nodeData.replace(";", "/");
                ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(nodeData);
                RpcEvent iRpcEvent = new RpcNodeChangeEvent(providerNodeInfo);
                RpcListenerLoader.sendEvent(iRpcEvent);
                watchNodeDataChange(serverNodePath);
            }
        });
    }

    //节点增删监控
    public void watchChildNodeData(String serverNodePath){
        zkClient.watchChildNodeData(serverNodePath, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //System.out.println(watchedEvent);
                String path = watchedEvent.getPath();
                List<String> childrenData = zkClient.getChildrenData(path);

                URLChangeWrapper urlChangeWrapper = new URLChangeWrapper();
                urlChangeWrapper.setServiceName(path.split("/")[2]);
                urlChangeWrapper.setProviderUrl(childrenData);

                RpcUpdateEvent rpcUpdateEvent = new RpcUpdateEvent(urlChangeWrapper);
                RpcListenerLoader.sendEvent(rpcUpdateEvent);
                //再次注册监听
                watchChildNodeData(path);
            }
        });
    }

    @Override
    public List<String> getProviderIps(String serviceName) {
        return zkClient.getChildrenData(ROOT + "/" + serviceName + "/provider");
    }

}
