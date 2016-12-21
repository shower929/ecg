package com.swm.core;

/**
 * Created by yangzhenyu on 2016/12/13.
 */

public class HrvData {
    public final float sdnn;
    public final float rmssd;

    HrvData(float sdnn, float rmssd) {
        this.sdnn = sdnn;
        this.rmssd = rmssd;
    }
}
