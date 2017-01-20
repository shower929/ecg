package com.swm.core;

import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.RmssdListener;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class RmssdService implements HeartRateListener {
    private RmssdListener mListener;

    @Override
    public void onHeartRateDataAvailable(HeartRateData heartRateData) {
        float rmssd = HeartRateService.GetRmssd();
        if (mListener != null)
            mListener.onRmssdAvailable(rmssd);
    }

    void setListener(RmssdListener listener) {
        mListener = listener;
    }

    void removeListener() {
        mListener = null;
    }
}
