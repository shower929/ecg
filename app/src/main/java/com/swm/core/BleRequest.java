package com.swm.core;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by yangzhenyu on 2016/9/28.
 */

class BleRequest {
    static final int WRITE_DESCRIPTOR = 1;
    static final int WRITE_CHARACTERISTIC = 2;
    static final int DESCRIPTOR_WRITE_DONE = 3;
    static final int CHARACTERISTIC_WRITE_DONE = 4;

    final int request;
    final BluetoothGattDescriptor descriptor;
    final BluetoothGattCharacteristic characteristic;

    static class Builder {
        int request;
        BluetoothGattDescriptor descriptor;
        BluetoothGattCharacteristic characteristic;

        Builder() {}
        Builder setRequest(int request) {
            this.request = request;
            return this;
        }

        Builder setDescriptor(BluetoothGattDescriptor descriptor) {
            this.descriptor = descriptor;
            return this;
        }

        Builder setCharacteristic(BluetoothGattCharacteristic characteristic) {
            this.characteristic = characteristic;
            return this;
        }

        BleRequest build() {
            return new BleRequest(request, descriptor, characteristic);
        }
    }

    BleRequest(int request, BluetoothGattDescriptor descriptor, BluetoothGattCharacteristic characteristic) {
        this.request = request;
        this.descriptor = descriptor;
        this.characteristic = characteristic;
    }


}
