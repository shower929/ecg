package com.swm.device;

/**
 * Created by yangzhenyu on 2016/10/11.
 */

public interface SwmDeviceListener {
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;

    void onConnectStateChanged(int state);
}
