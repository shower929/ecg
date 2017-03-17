package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

public class BleService implements SwmService {

    private BleListener bleListener;

    BleService(SwmClient client) {
    }

    @Override
    public void setListener(SwmListener listener) {
    }

    @Override
    public void process(BleData data) {
        bleListener.onRawDataAvailable(data.uuid, data.rawData);
    }

    public void setBleListener(BleListener bleListener) {
        this.bleListener = bleListener;
    }
}
