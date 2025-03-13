package com.pwc.client;

import java.util.HashMap;
import java.util.Map;

public class RpcReferenceWrapper<T> {
    private Class<T> aimClass;

    private Map<String, Object> attachments = new HashMap<>();

    public Class<T> getAimClass() {
        return aimClass;
    }

    public void setAimClass(Class<T> aimClass) {
        this.aimClass = aimClass;
    }

    public boolean isAsync(){
        return Boolean.valueOf(String.valueOf(attachments.get("async")));
    }

    public void setAsync(boolean async){
        this.attachments.put("async",async);
    }

    public String getUrl(){
        return String.valueOf(attachments.get("url"));
    }

    public void setUrl(String url){
        attachments.put("url",url);
    }

    public String getServiceToken(){
        return String.valueOf(attachments.get("serviceToken"));
    }

    public void setServiceToken(String serviceToken){
        attachments.put("serviceToken",serviceToken);
    }

    public String getGroup(){
        return String.valueOf(attachments.get("group"));
    }

    public void setGroup(String group){
        attachments.put("group",group);
    }

    public Map<String, Object> getAttatchments() {
        return attachments;
    }

    public void setAttatchments(Map<String, Object> attatchments) {
        this.attachments = attatchments;
    }

    public void setTimeOut(int timeOut){
        this.attachments.put("timeout", timeOut);
    }

    public String getTimeOut(){
        return String.valueOf(attachments.get("timeout"));
    }

    public void setRetry(int retry){
        attachments.put("retry", retry);
    }

    public int getRetry(){
        Object retry = attachments.get("retry");
        return retry == null ? 0 : (int) retry;
    }
}
