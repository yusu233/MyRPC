package com.pwc.router.random;

import com.pwc.common.ChannelFutureWrapper;
import com.pwc.router.Router;
import com.pwc.router.Selector;
import com.pwc.registry.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.pwc.common.cache.CommonClientCache.CHANNEL_FUTURE_POLLING_REF;
import static com.pwc.common.cache.CommonClientCache.CONNECT_MAP;
import static com.pwc.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

/**
 * 随机路由策略
 */
public class RandomRouterImpl implements Router {
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        int size = channelFutureWrappers.size();
        int[] randomIndex = createRandomIndex(size);
        ChannelFutureWrapper[] arr = new ChannelFutureWrapper[size];
        for(int i = 0; i < size; i++){
            arr[i] = channelFutureWrappers.get(randomIndex[i]);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), arr);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getProviderServiceName());
    }

    @Override
    public void updateWeight(URL url) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(url.getServiceName());
        Integer[] weightArr = createWeightArr(channelFutureWrappers);
        Integer[] arr = createRandomArr(weightArr);
        ChannelFutureWrapper[] newChannelFutureWrappers = new ChannelFutureWrapper[arr.length];
        for(int i = 0; i < arr.length; i++){
            newChannelFutureWrappers[i] = channelFutureWrappers.get(arr[i]);
        }
        SERVICE_ROUTER_MAP.put(url.getServiceName(), newChannelFutureWrappers);
    }

    private static Integer[] createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers){
        ArrayList<Integer> weightArr = new ArrayList<>();
        for(int i = 0; i < channelFutureWrappers.size(); i++){
            Integer weight = channelFutureWrappers.get(i).getWeight();
            int c = weight / 100;
            for(int j = 0; j < c; j++){
                weightArr.add(i);
            }
        }
        Integer[] arr = new Integer[weightArr.size()];
        return weightArr.toArray(arr);
    }

    private static Integer[] createRandomArr(Integer[] arr) {
        int total = arr.length;
        Random random = new Random();
        for (int i = 0; i < total; i++) {
            int j = random.nextInt(total);
            if (i == j) {
                continue;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    private int[] createRandomIndex(int size){
        int[] result = new int[size];
        Arrays.fill(result, -1);
        Random random = new Random();

        int index = 0;
        while(index < size){
            int num = random.nextInt(size);
            if(!isSame(result, num)) result[index++] = num;
        }
        return result;
    }

    private boolean isSame(int[] arr, int val){
        for(int i : arr){
            if(i == val) return true;
        }
        return false;
    }
}
