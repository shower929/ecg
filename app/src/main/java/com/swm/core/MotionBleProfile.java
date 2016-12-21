package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class MotionBleProfile {
    static final UUID SERVICE = UUID.fromString("F000AA80-0451-4000-B000-000000000000");
    static final UUID DATA = UUID.fromString("F000AA81-0451-4000-B000-000000000000");
    static final UUID CONF = UUID.fromString("F000AA82-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mDataCharacteristic;
    private BluetoothGattCharacteristic mConfCharacteristic;

    MotionBleProfile(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUuid().equals(DATA)) {
                mDataCharacteristic = characteristic;
            }

            if (characteristic.getUuid().equals(CONF)) {
                mConfCharacteristic = characteristic;
            }
        }
    }

    void enableNotification(BluetoothGatt gatt) throws Exception {
        if(!gatt.setCharacteristicNotification(mDataCharacteristic, true))
            throw new Exception("Set bluetooth notification fail");
        BluetoothGattDescriptor descriptor = mDataCharacteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        BleRequest.Builder builder = new BleRequest.Builder();
        builder.setRequest(BleRequest.WRITE_DESCRIPTOR)
                .setDescriptor(descriptor);
        SwmDeviceController.getIns().request(builder.build());
    }

    void enableService() {
        byte[] val = new byte[1];
        val[0] = (byte) 0x07;
        mConfCharacteristic.setValue(val);
        BleRequest.Builder builder = new BleRequest.Builder();
        builder.setRequest(BleRequest.WRITE_CHARACTERISTIC)
                .setCharacteristic(mConfCharacteristic);
        SwmDeviceController.getIns().request(builder.build());
    }
}
