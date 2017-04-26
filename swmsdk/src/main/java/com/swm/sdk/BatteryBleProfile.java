package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BatteryBleProfile extends GenericBleProfile{
    static final UUID SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    static final UUID DATA = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic mBatteryPercentChar;

    BatteryBleProfile(BleDevice device, BluetoothGattService service, BluetoothGatt gatt) {
        super(device, service, gatt);
    }


    @Override
    @Nullable
    byte[] getEnableData() {
        return null;
    }

    @Override
    @Nullable
    byte[] getDisableData() {
        return null;
    }

    @Override
    UUID getDataUuid() {
        return DATA;
    }

    @Override
    @Nullable
    UUID getConfigUuid() {
        return null;
    }
}
