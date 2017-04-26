package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

abstract class GenericSwmDeviceModule extends SwmModule{
    static SwmDevice DEVICE;

    public synchronized static SwmDevice getDevice() {
        return DEVICE;
    }

    public void connect(DeviceCallback callback) {
        DEVICE.connect(callback);
    }

    public void setServiceListener(DeviceListener listener) {
        DEVICE.setListener(listener);
    }

    public void removeListener() {
        DEVICE.removeListener();
    }

    public boolean isConnected() {
        return DEVICE.isConnected();
    }

    public void disconnect() {
        DEVICE.disconnect();
    }

}
