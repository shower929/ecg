package com.swm.sdk;

import android.content.IntentFilter;

/**
 * Created by yangzhenyu on 2017/4/22.
 */

public class HilbertHuangTransform extends Filter {
    private float standardDeviation;
    private float upper;
    private float lower;
    private float mean;
    private float imf1;

    public HilbertHuangTransform(float standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @Override
    public Integer filter(Integer value) {
        Integer sd;
        do {
            upper = cubicSpinLine(value);
            lower = localMinima(value);
            mean = (upper + lower)/2;
            imf1 = value - mean;
            return imf1;
        }while(sd > standardDeviation);
    }

    private float cubicSpinLine(Integer value) {
        return
    }


}
