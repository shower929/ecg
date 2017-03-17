package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BleCommandHelper {

    static BleCommand getEnableCommand(BluetoothGattCharacteristic characteristic) {
        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.WRITE)
                .setCharacteristic(characteristic);
        return builder.build();
    }

    static BleCommand getNotificationCommand(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) throws Exception{
        if(!gatt.setCharacteristicNotification(characteristic, true))
            throw new Exception("Set bluetooth notification fail");

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.ENABLE_NOTI)
                .setDescriptor(descriptor);
        return builder.build();
    }

    static BleCommand getReadCommand(BluetoothGattCharacteristic characteristic) {
        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.READ)
                .setCharacteristic(characteristic);
        return builder.build();
    }
}
