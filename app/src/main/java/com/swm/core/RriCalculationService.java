package com.swm.core;

import com.swm.heartbeat.HeartRateListener;

import static com.swm.core.SWM_COM_HEADER.HRV_RRI_LIMIT_BUF;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class RriCalculationService implements HeartRateListener {
    private RtoRintervalDataListener mListener;

    void setListener(RtoRintervalDataListener listener) {
        mListener = listener;
    }

    void removeListener() {
        mListener = null;
    }

    @Override
    public void onHeartRateDataAvailable(HeartRateData heartRateData) {
        double[] rriAry = new double[HRV_RRI_LIMIT_BUF];
        double[] timeAry = new double[HRV_RRI_LIMIT_BUF];
        HeartRateService.GetRtoRIntervalData(rriAry, timeAry);
        if (mListener != null)
            mListener.onRtoRintervalDataAvailable(new RtoRintervalData(rriAry, timeAry));
    }
}
