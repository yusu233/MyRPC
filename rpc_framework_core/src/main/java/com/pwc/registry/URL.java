package com.pwc.registry;

import com.pwc.registry.zookeeper.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端信息配置类
 */
public class URL {
    private String applicationName;
    private String serviceName;
    private Map<String, String> params = new HashMap<>();

    public void addParam(String key, String value){
        this.params.putIfAbsent(key, value);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public static String generateProviderStr(URL url){
        String host = url.getParams().get("host");
        String port = url.getParams().get("port");
        String group = url.getParams().get("group");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";"
                            + host + ":" + port + ";" + System.currentTimeMillis() + ";100;" + group).getBytes(), StandardCharsets.UTF_8);
    }

    public static String generateConsumerStr(URL url){
        String host = url.getParams().get("host");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";"
                            + host + ";" + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);
    }

    public static ProviderNodeInfo buildURLFromUrlStr(String providerNodeStr) {
        String[] items = providerNodeStr.split(";");
        for (String item : items) {
            System.out.println("item = " + item);
        }
        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(items[1]);
        providerNodeInfo.setAddress(items[2]);
        providerNodeInfo.setRegistryTime(items[3]);
        providerNodeInfo.setWeight(Integer.valueOf(items[4]));
        providerNodeInfo.setGroup(String.valueOf(items[5]));
        return providerNodeInfo;
    }
}
