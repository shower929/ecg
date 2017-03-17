package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by yangzhenyu on 2016/9/28.
 */

class BleCommand {
    static final int ENABLE_NOTI = 1;
    static final int WRITE = 2;
    static final int ENABLE_NOTI_DONE = 3;
    static final int WRITE_DONE = 4;
    static final int READ = 5;
    static final int READ_DONE = 6;
    int command;
    final BluetoothGattDescriptor descriptor;
    final BluetoothGattCharacteristic characteristic;

    static class Builder {
        int command;
        BluetoothGattDescriptor descriptor;
        BluetoothGattCharacteristic characteristic;

        Builder() {}
        Builder setCommand(int command) {
            this.command = command;
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

        BleCommand build() {
            return new BleCommand(command, descriptor, characteristic);
        }
    }

    BleCommand(int command, BluetoothGattDescriptor descriptor, BluetoothGattCharacteristic characteristic) {
        this.command = command;
        this.descriptor = descriptor;
        this.characteristic = characteristic;
    }


}
