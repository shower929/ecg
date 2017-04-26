package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

public interface SwmDevice {
    void connect(DeviceCallback callback);
    void disconnect();
    void setListener(DeviceListener listener);
    void removeListener();
    boolean isConnected();
    void enableEcgService(boolean enable) throws Exception;
    void enableMotionService(boolean enable) throws Exception;
    void enablePressureService(boolean enable) throws Exception;
    boolean isEcgServiceEnable();
    boolean isMotionServiceEnable();
    boolean isPressureServiceEnable();
}
