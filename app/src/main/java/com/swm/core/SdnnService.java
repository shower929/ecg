package com.swm.core;

import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.SdnnListener;
import com.swm.sdk.HeartRateData;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class SdnnService implements HeartRateListener {
    private SdnnListener mListener;

    @Override
    public void onHeartRateDataAvailable(HeartRateData heartRateData) {
        float sdnn = HeartRateService.GetSdnn();
        if(mListener != null)
            mListener.onSdnnAvailable(sdnn);
    }

    void setListener(SdnnListener listener) {
        mListener = listener;
    }

    void removeListener() {
        mListener = null;
    }
}
