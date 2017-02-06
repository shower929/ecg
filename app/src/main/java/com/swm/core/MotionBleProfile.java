package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class MotionBleProfile {
    static final UUID SERVICE = UUID.fromString("F000AA80-0451-4000-B000-000000000000");

    static final UUID DATA = UUID.fromString("F000AA81-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mMotionDataChar;

    static final UUID CONF = UUID.fromString("F000AA82-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mMotionConfChar;

    private static final byte[] ENABLE_VALUE = {(byte)0x07};

    MotionBleProfile(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(DATA)) {
                mMotionDataChar = characteristic;
            }

            if (characteristic.getUuid().equals(CONF)) {
                mMotionConfChar = characteristic;
            }
        }
    }

    void enableMotionNotification(BluetoothGatt gatt) throws Exception{
        BleProfileHelper.enableNotification(gatt, mMotionDataChar);
    }

    void enableMotionService() {
        mMotionConfChar.setValue(ENABLE_VALUE);
        BleProfileHelper.enableService(mMotionConfChar);
    }
}
