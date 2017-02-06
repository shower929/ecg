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

class EcgBleProfile {
    static final UUID SERVICE = UUID.fromString("F000AA70-0451-4000-B000-000000000000");

    static final UUID DATA = UUID.fromString("F000AA71-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mEcgDataChar;

    static final UUID CONF = UUID.fromString("F000AA72-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mEcgConfChar;

    private static final byte[] ENABLE_VALUE = {(byte)0x01};

    EcgBleProfile(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(DATA)) {
                mEcgDataChar = characteristic;
            }

            if (characteristic.getUuid().equals(CONF)) {
                mEcgConfChar = characteristic;
            }
        }
    }

    void enableEcgService() {
        mEcgConfChar.setValue(ENABLE_VALUE);
        BleProfileHelper.enableService(mEcgConfChar);
    }

    void enableEcgNotification(BluetoothGatt gatt) throws Exception {
        BleProfileHelper.enableNotification(gatt, mEcgDataChar);
    }

}
