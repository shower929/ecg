package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BleCommandHelper {

    static BleCommand getWriteCommand(BluetoothGattCharacteristic characteristic) {
        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.WRITE)
                .setCharacteristic(characteristic);
        return builder.build();
    }

    static BleCommand getEnableNotificationCommand(BluetoothGattCharacteristic characteristic) throws Exception{
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.NOTIFICATION)
                .setDescriptor(descriptor);
        return builder.build();
    }

    static BleCommand getDisableNotificationCommand(BluetoothGattCharacteristic characteristic) throws Exception{
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        BleCommand.Builder builder = new BleCommand.Builder();
        builder.setCommand(BleCommand.NOTIFICATION)
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
