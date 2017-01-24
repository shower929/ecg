package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/9/28.
 */

class EcgBleProfile extends BaseBleProfile{
    static final String SERVICE = "F000AA70-0451-4000-B000-000000000000";
    static final String DATA = "F000AA71-0451-4000-B000-000000000000";
    static final String CONF = "F000AA72-0451-4000-B000-000000000000";
    private static final byte[] ENABLE_VALUE = {(byte)0x01};

    EcgBleProfile(BluetoothGattService service) {
        super(service, DATA, CONF, ENABLE_VALUE);
    }

}
