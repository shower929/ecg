package com.swm.hrv;

/**
 * Created by yangzhenyu on 2017/1/4.
 */

public interface RriListener {
    void onRriBinsDataAvailable(double[] rriCount, double[] rriTime);
}
