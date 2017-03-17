package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class MotionBleProfile extends GenericBleProfile{
    static final UUID SERVICE = UUID.fromString("F000AA80-0451-4000-B000-000000000000");

    static final UUID DATA = UUID.fromString("F000AA81-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mMotionDataChar;

    static final UUID CONF = UUID.fromString("F000AA82-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mMotionConfChar;

    private static final byte[] ENABLE_VALUE = {(byte)0x07};
    private static final byte[] DISABLE_VALUE = {(byte)0x00};
    private BleDevice device;

    MotionBleProfile(BluetoothGattService service, BleDevice device) {
        this.device = device;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(DATA)) {
                mMotionDataChar = characteristic;
            }

            if (characteristic.getUuid().equals(CONF)) {
                mMotionConfChar = characteristic;
            }
        }
    }

    void enableMotionNotification(BluetoothGatt gatt, BleDevice device) throws Exception{
        device.sendCommand(BleCommandHelper.getNotificationCommand(gatt, mMotionDataChar));
    }

    synchronized void enableService() {

        mMotionConfChar.setValue(ENABLE_VALUE);
        device.sendCommand(BleCommandHelper.getEnableCommand(mMotionConfChar));

    }

    synchronized void disableService() {

        mMotionConfChar.setValue(DISABLE_VALUE);
        device.sendCommand(BleCommandHelper.getEnableCommand(mMotionConfChar));
    }

    @Override
    byte[] getEnableData() {
        return ENABLE_VALUE;
    }
}
