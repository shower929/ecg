package com.swm.core;

import com.swm.hrv.RriFrequencyListener;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class RriFrequencyService implements RtoRintervalDataListener {
    RriFrequencyListener mListener;

    @Override
    public synchronized void onRtoRintervalDataAvailable(RtoRintervalData data) {
        double[] frequencyData = new double[5];
        HrvService.GetFrequencyData(data.rriAry, data.timeAry, frequencyData);
        if (mListener != null) {
            mListener.onFrequencyDataAvailable(frequencyData);
        }
    }

    synchronized void setListener(RriFrequencyListener listener) {
        mListener = listener;
    }

    synchronized void removeListener() {
        mListener = null;
    }
}
