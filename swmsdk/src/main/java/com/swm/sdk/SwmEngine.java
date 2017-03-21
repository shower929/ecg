package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */
interface SwmEngine {
    enum ServiceType {ECG, MOTION, RUNNING, BLE};

    void setListener(SwmListener listener);
    void process(BleData data);
    ServiceType getServiceType();
}
