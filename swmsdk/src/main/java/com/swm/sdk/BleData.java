package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/9/25.
 */
class BleData {
    public final UUID uuid;
    public byte[] rawData;

    BleData(UUID uuid, byte[] rawData) {
        this.uuid = uuid;
        this.rawData = rawData;
    }

    static BleData from (final BluetoothGattCharacteristic characteristic) {
        return new BleData(characteristic.getUuid(), characteristic.getValue());
    }
}
