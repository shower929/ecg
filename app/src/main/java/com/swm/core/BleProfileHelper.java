package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BleProfileHelper {

    static void enableService(BluetoothGattCharacteristic characteristic) {
        BleRequest.Builder builder = new BleRequest.Builder();
        builder.setRequest(BleRequest.WRITE_CHARACTERISTIC)
                .setCharacteristic(characteristic);
        SwmDeviceController.getIns().request(builder.build());
    }

    static void enableNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) throws Exception{
        if(!gatt.setCharacteristicNotification(characteristic, true))
            throw new Exception("Set bluetooth notification fail");

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        BleRequest.Builder builder = new BleRequest.Builder();
        builder.setRequest(BleRequest.WRITE_DESCRIPTOR)
                .setDescriptor(descriptor);
        SwmDeviceController.getIns().request(builder.build());
    }

    static void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        BleRequest.Builder builder = new BleRequest.Builder();
        builder.setRequest(BleRequest.READ_CHARACTERISTIC)
                .setCharacteristic(characteristic);
        SwmDeviceController.getIns().request(builder.build());
    }
}
