package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

class GyroData extends MotionData {
    static GyroData from(BleData data) {
        return new GyroData(((data.rawData[1] << 8) & 0xFF00) | (data.rawData[0] & 0xFF)
                , ((data.rawData[3] << 8) & 0xFF00) | (data.rawData[2] & 0xFF)
                , ((data.rawData[5] << 8) & 0xFF00) | (data.rawData[4] & 0xFF));
    }

    GyroData(int x, int y, int z) {
        super(x, y, z);
    }
}
