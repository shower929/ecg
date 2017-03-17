package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2016/9/28.
 */

class EcgBleProfile extends GenericBleProfile {
    static final UUID SERVICE = UUID.fromString("F000AA70-0451-4000-B000-000000000000");

    static final UUID DATA = UUID.fromString("F000AA71-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mEcgDataChar;

    static final UUID CONF = UUID.fromString("F000AA72-0451-4000-B000-000000000000");
    private BluetoothGattCharacteristic mEcgConfChar;

    private static final byte[] ENABLE_VALUE = {(byte)0x01};
    private static final byte[] DISABLE_VALUE = {(byte)0x00};

    private BleDevice device;

    EcgBleProfile(BluetoothGattService service, BleDevice device) {
        this.device = device;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(DATA)) {
                mEcgDataChar = characteristic;
            }

            if (characteristic.getUuid().equals(CONF)) {
                mEcgConfChar = characteristic;
            }
        }
    }

    synchronized void enableService() {
        mEcgConfChar.setValue(ENABLE_VALUE);
        device.sendCommand(BleCommandHelper.getEnableCommand(mEcgConfChar));
    }

    synchronized void disableService() {
        mEcgConfChar.setValue(DISABLE_VALUE);
        device.sendCommand(BleCommandHelper.getEnableCommand(mEcgConfChar));
    }

    void enableEcgNotification(BluetoothGatt gatt, BleDevice device) throws Exception {

        device.sendCommand(BleCommandHelper.getNotificationCommand(gatt, mEcgDataChar));
    }


    @Override
    byte[] getEnableData() {
        return ENABLE_VALUE;
    }
}
