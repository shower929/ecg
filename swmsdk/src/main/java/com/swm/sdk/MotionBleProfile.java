package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class MotionBleProfile extends GenericBleProfile{
    static final UUID SERVICE = UUID.fromString("F000AA80-0451-4000-B000-000000000000");

    static final UUID DATA = UUID.fromString("F000AA81-0451-4000-B000-000000000000");

    static final UUID CONF = UUID.fromString("F000AA82-0451-4000-B000-000000000000");

    private static final byte[] ENABLE_VALUE = {(byte)0x07};
    private static final byte[] DISABLE_VALUE = {(byte)0x00};

    public MotionBleProfile(BleDevice device, BluetoothGattService service, BluetoothGatt gatt) {
        super(device, service, gatt);
    }

    @Override
    byte[] getEnableData() {
        return ENABLE_VALUE;
    }

    @Override
    byte[] getDisableData() {
        return DISABLE_VALUE;
    }

    @Override
    UUID getDataUuid() {
        return DATA;
    }

    @Override
    UUID getConfigUuid() {
        return CONF;
    }
}
