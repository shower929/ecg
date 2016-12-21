package com.swm.core;

/**
 * Created by yangzhenyu on 2016/12/7.
 */

class BleDumpData extends DumpData {
    private final byte[] data;
    BleDumpData(double bps, double lossRate, double byteErrorRate) {
        data = new String(bps + "," + lossRate + "," + byteErrorRate).getBytes();
    }
    @Override
    byte[] dump() {
        return data;
    }
}
