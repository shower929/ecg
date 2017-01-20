package com.swm.core;

import com.swm.hrv.RriDistributionListener;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class RriDistributionService implements RtoRintervalDataListener {
    private RriDistributionListener mListener;

    @Override
    public synchronized void onRtoRintervalDataAvailable(RtoRintervalData data) {
        int size = HrvService.GetRriDistributionSize(data.rriAry);
        double[] binAry = new double[size];
        double[] binIdx = new double[size];
        HrvService.GetRriDistribution(data.rriAry, data.timeAry, binAry, binIdx, size);
        if (mListener != null)
            mListener.onRriDistributionChanged(binAry, binIdx);
    }

    synchronized void setListener(RriDistributionListener listener) {
        mListener = listener;
    }

    synchronized void removeListener() {
        mListener = null;
    }
}
