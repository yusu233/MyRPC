package com.pwc.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader {
    private static final String PREFIX = "META-INF/rpc/";
    public static Map<String, LinkedHashMap<String, Class>> EXTENSION_LOADER_CLASS_CACHE = new ConcurrentHashMap<>();

    public void loadExtension(Class<?> service) throws IOException, ClassNotFoundException {
        if(service == null){
            throw new IllegalArgumentException("service is null");
        }
        String spiFilePath = PREFIX + service.getName();
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> enumeration = classLoader.getResources(spiFilePath);
        while(enumeration.hasMoreElements()){
            URL url = enumeration.nextElement();
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            LinkedHashMap<String, Class> classMap = new LinkedHashMap<>();
            while((line = bufferedReader.readLine()) != null){
                if(line.startsWith("#")) continue;

                String[] split = line.split("=");
                String classKey = split[0];
                String className = split[1];
                classMap.put(classKey, Class.forName(className));
            }

            if(EXTENSION_LOADER_CLASS_CACHE.containsKey(service.getName())){
                EXTENSION_LOADER_CLASS_CACHE.get(service.getName()).putAll(classMap);
            }else {
                EXTENSION_LOADER_CLASS_CACHE.put(service.getName(), classMap);
            }
        }

    }
}
