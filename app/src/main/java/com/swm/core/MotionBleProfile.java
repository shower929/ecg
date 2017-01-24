package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class MotionBleProfile extends BaseBleProfile{
    static final String SERVICE = "F000AA80-0451-4000-B000-000000000000";
    static final String DATA = "F000AA81-0451-4000-B000-000000000000";
    static final String CONF = "F000AA82-0451-4000-B000-000000000000";
    private static final byte[] ENABLE_VALUE = {(byte)0x07};

    MotionBleProfile(BluetoothGattService service) {
        super(service, DATA, CONF, ENABLE_VALUE);
    }
}
