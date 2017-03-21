package com.swm.sdk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.swm.device.BuildConfig;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

public class SwmDeviceModule {
    private static final String LOG_TAG = "SwmDevice";
    private static SwmDeviceModule SWM_DEVICE_MODULE;

    private SwmDevice mSwmDevice;
    private SwmEngine service;

    private SwmDeviceModule(SwmClient client){
        service = SwmServiceProvider.getIns().getService(client);
    }

    public synchronized static void init(SwmClient client)  {
        if(SWM_DEVICE_MODULE == null)
            SWM_DEVICE_MODULE = new SwmDeviceModule(client);
    }

    public synchronized static SwmDeviceModule getIns() {
        return SWM_DEVICE_MODULE;
    }

    public void connectBle(Context context, BluetoothDevice device, DeviceCallback callback) {
        mSwmDevice = new OneFiveZeroOne(context, device, service);
        mSwmDevice.connect(callback);

    }

    public void setServiceListener(SwmListener listener) {
        mSwmDevice.setListener(listener);
    }

    public void removeListener() {
        mSwmDevice.removeListener();
    }

    public boolean isConnected() {
        return mSwmDevice.isConnected();
    }

    public void disconnect() {
        mSwmDevice.disconnect();
    }

    public SwmDevice getDevice() {
        return mSwmDevice;
    }

    public String version() {
        return BuildConfig.VERSION_NAME;
    }
}
