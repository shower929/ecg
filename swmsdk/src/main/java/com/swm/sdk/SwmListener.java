package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/13.
 */

public interface SwmListener {
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    enum Service {ECG, MOTION, PRESSURE};

    void onConnectStateChanged(int state);
    void onDataAvailable(SwmData data);
    void onServiceStateChange(Service service, boolean enable);

}
