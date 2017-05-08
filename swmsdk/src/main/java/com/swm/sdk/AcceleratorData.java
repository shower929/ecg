package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

class AcceleratorData extends MotionData {
    static AcceleratorData from(BleData data) {
        return new AcceleratorData(((data.rawData[7] << 8) & 0xFF00) | (data.rawData[6] & 0xFF)
                , ((data.rawData[9] << 8) & 0xFF00) | (data.rawData[8] & 0xFF)
                , ((data.rawData[11] << 8 )& 0xFF00) | (data.rawData[10] & 0xFF));
    }

    private AcceleratorData(int x, int y, int z) {
        super(x, y, z);
    }
}
