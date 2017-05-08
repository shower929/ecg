package com.swm.engineering.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by yangzhenyu on 2017/3/16.
 */

public interface BleManagerCallback {
    void onDeviceFound(BluetoothDevice device);
    void onListDevice(BluetoothDevice[] devices);
}
