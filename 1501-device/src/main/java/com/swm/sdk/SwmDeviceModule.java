package com.swm.sdk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

public class SwmDeviceModule extends GenericSwmDeviceModule{

    public static synchronized SwmDevice newDevice(Context context, BluetoothDevice bleDevice) {
        checkPermission(context);
        if(DEVICE == null)
            DEVICE = new OneFiveZeroOne(context, bleDevice);
        return DEVICE;
    }
}
