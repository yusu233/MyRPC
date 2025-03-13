package com.pwc.router;


import com.pwc.common.ChannelFutureWrapper;
import com.pwc.registry.URL;

public interface Router {
    void refreshRouterArr(Selector selector);
    ChannelFutureWrapper select(Selector selector);
    void updateWeight(URL url);
}
