package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */
interface SwmService {
    void setListener(SwmListener listener);
    void process(BleData data);
}
