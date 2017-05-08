package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

class MagneticData extends MotionData {
    static MagneticData from(BleData data) {
        return new MagneticData(((data.rawData[13] << 8) & 0xFF00) | (data.rawData[12] & 0xFF)
                , ((data.rawData[15] << 8) & 0xFF00) | (data.rawData[14] & 0xFF)
                , ((data.rawData[17] << 8) & 0xFF00) | (data.rawData[16] & 0xFF));
    }

    private MagneticData(int x, int y, int z) {
        super(x, y, z);
    }
}
