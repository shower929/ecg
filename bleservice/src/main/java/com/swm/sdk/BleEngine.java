package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

public class BleEngine implements SwmEngine {

    private BleListener bleListener;

    BleEngine(SwmClient client) {
    }

    @Override
    public void setListener(SwmListener listener) {
    }

    @Override
    public void process(BleData data) {
        bleListener.onRawDataAvailable(data.uuid, data.rawData);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.BLE;
    }

    public void setBleListener(BleListener bleListener) {
        this.bleListener = bleListener;
    }
}
