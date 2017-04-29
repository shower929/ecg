package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/13.
 */

public interface DeviceListener {

    enum Service {ECG, MOTION, PRESSURE};

    void onConnectionStateChanged(int state);
    void onServiceStateChange(Service service, boolean enable);

}
