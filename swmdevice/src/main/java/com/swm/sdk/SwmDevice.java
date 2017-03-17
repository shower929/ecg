package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

public interface SwmDevice {
    void connect(DeviceCallback callback);
    void disconnect();
    void setListener(SwmListener listener);
    void removeListener();
    boolean isConnected();
    void enableEcg(boolean enable);
    void enableMotion(boolean enable);
    boolean isEcgEnable();
    boolean isMotionEnable();
}
