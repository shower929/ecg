package com.swm.core;

import com.swm.sdk.DumpData;

/**
 * Created by yangzhenyu on 2016/12/7.
 */

class BleDumpData extends DumpData {
    private final byte[] data;
    BleDumpData(double bps, double lossRate, double byteErrorRate, double latency) {
        data = new String(bps + "," + lossRate + "," + byteErrorRate + "," + latency).getBytes();
    }
    @Override
    byte[] dump() {
        return data;
    }
}
