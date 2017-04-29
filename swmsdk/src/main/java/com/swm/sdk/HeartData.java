package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class HeartData extends DumpData {
    public final int heartRate;
    public final float sdnn;
    public final float rmssd;

    HeartData(int heartRate, float sdnn, float rmssd) {
        this.heartRate = heartRate;
        this.sdnn = sdnn;
        this.rmssd = rmssd;
    }

    public boolean hasHeartRate() {
        return heartRate != 0;
    }

    @Override
    byte[] dump() {
        String dump = "{\"HeartData\":{\"HeartRate\":" + heartRate
                                    + ", \"SDNN\":" + sdnn
                                    + ", \"RMSSD\":" + rmssd + "}}";
        return dump.getBytes();
    }
}
