package com.swm.hrv;

/**
 * Created by yangzhenyu on 2017/1/6.
 */

public interface FrequencyListener {
    void onFrequencyDataAvailable(double[] frequencyData);
}
