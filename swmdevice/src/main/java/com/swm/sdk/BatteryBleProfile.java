package com.swm.sdk;

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
    private BleDevice device;

    BatteryBleProfile(BluetoothGattService service, BleDevice device) {
        this.device = device;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUuid().equals(BATTERY_PERCENT)) {
                mBatteryPercentChar = characteristic;
            }
        }
    }

    void enableBatteryPercentNoti(BluetoothGatt gatt, BleDevice device) throws Exception{
        BleCommand command = BleCommandHelper.getNotificationCommand(gatt, mBatteryPercentChar);
        device.sendCommand(command);
    }
}
