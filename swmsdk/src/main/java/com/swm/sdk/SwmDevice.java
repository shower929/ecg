package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

public interface SwmDevice {
    public static final String ACTION_DEVICE_EVENT = "action_connection_event";
    public static final String EXTRA_CONNECTION_STATUS = "extra_connection_status";
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;

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
