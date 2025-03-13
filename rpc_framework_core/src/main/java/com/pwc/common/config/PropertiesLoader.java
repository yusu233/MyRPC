package com.pwc.common.config;

import com.pwc.common.utils.CommonUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesLoader {
    private static Properties properties;
    private static HashMap<String, String> propertiesMap = new HashMap<>();
    private static final String RPC_PROPERTIES_FILEPATH = "D:\\Code\\rpc_learn\\rpc_framework_core\\src\\main\\resources\\rpc.properties";



    /**
     * 从配置文件加载配置信息
     * @throws IOException
     */
    public static void loadConfiguration() throws IOException {
        if(properties != null) return;

        properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(RPC_PROPERTIES_FILEPATH);
        properties.load(fileInputStream);
    }

    /**
     * 获取String类型的配置信息
     * @param key
     * @return
     */
    public static String getPropertiesStr(String key) {
        if(properties == null) return null;
        if(CommonUtil.isEmpty(key)) return null;
        if(!propertiesMap.containsKey(key)){
            propertiesMap.put(key, (String) properties.get(key));
        }
        return propertiesMap.get(key);
    }

    /**
     * 获取Integer类型的配置信息
     * @param key
     * @return
     */
    public static Integer getPropertiesInteger(String key) {
        if(properties == null) return null;
        if(CommonUtil.isEmpty(key)) return null;
        if(!propertiesMap.containsKey(key)){
            propertiesMap.put(key, (String) properties.get(key));
        }
        return Integer.valueOf(propertiesMap.get(key));
    }
}
