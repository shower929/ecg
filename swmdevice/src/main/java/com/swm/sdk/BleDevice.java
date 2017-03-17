package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

interface BleDevice {
    void sendCommand(BleCommand command);
}
