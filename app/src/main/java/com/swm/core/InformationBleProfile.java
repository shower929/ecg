package com.swm.core;

import android.bluetooth.BluetoothGattService;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class InformationBleProfile extends BaseBleProfile {
    static final String SERVICE = "0000180A";
    static final String SYSTEM_ID = "00002A23";
    static final String MODEL_NUMBER = "00002A24";
    static final String SERIAL_NUMBER = "00002A25";
    static final String HARDWARE_REVISION = "00002A27";
    static final String FIRMWARE_REVISION = "00002A26";
    static final String SOFTWARE_REVISION = "00002A28";
    static final String MANUFACTURER_NAME = "00002A29";
    static final String SWM_11073_CERT_DATA = "20000A2A";
    static final String PNP_ID = "00002A50";

    InformationBleProfile(BluetoothGattService service) {
        super(service, FIRMWARE_REVISION);
    }
}
