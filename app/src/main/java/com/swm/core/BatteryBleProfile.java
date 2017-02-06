package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BatteryBleProfile {
    static final UUID SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    static final UUID BATTERY_PERCENT = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic mBatteryPercentChar;

    BatteryBleProfile(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUuid().equals(BATTERY_PERCENT)) {
                mBatteryPercentChar = characteristic;
            }
        }
    }

    void enableBatteryPercentNoti(BluetoothGatt gatt) throws Exception{
        BleProfileHelper.enableNotification(gatt, mBatteryPercentChar);
    }

    void readBatteryPercent(BluetoothGatt gatt) {
        gatt.readCharacteristic(mBatteryPercentChar);
    }
}
