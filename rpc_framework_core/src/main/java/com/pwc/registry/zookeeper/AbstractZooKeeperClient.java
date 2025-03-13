package com.pwc.registry.zookeeper;

import java.util.List;
import org.apache.zookeeper.Watcher;

public abstract class AbstractZooKeeperClient {
    private String zkAddress;
    private int baseSleepTimes = 1000;
    private int maxRetryTimes = 3;

    public AbstractZooKeeperClient(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public AbstractZooKeeperClient(String zkAddress, Integer baseSleepTimes, Integer maxRetryTimes) {
        this.zkAddress = zkAddress;
        if (baseSleepTimes == null) {
            this.baseSleepTimes = 1000;
        } else {
            this.baseSleepTimes = baseSleepTimes;
        }
        if (maxRetryTimes == null) {
            this.maxRetryTimes = 3;
        } else {
            this.maxRetryTimes = maxRetryTimes;
        }
    }

    public int getBaseSleepTimes() {
        return baseSleepTimes;
    }

    public void setBaseSleepTimes(int baseSleepTimes) {
        this.baseSleepTimes = baseSleepTimes;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public abstract void updateNodeData(String address, String data);
    public abstract Object getClient();
    public abstract String getNodeData(String path);
    public abstract List<String> getChildrenData(String path);
    public abstract void createPersistentData(String address, String data);
    public abstract void createPersistentWithSeqData(String address, String data);
    public abstract void createTemporarySeqData(String address, String data);
    public abstract void createTemporaryData(String address, String data);
    public abstract void setTemporaryData(String address, String data);
    public abstract void destroy();
    public abstract List<String> listNode(String address);
    public abstract boolean deleteNode(String address);
    public abstract boolean existNode(String address);
    public abstract void watchNodeData(String path, Watcher watcher);
    public abstract void watchChildNodeData(String path, Watcher watcher);
}
