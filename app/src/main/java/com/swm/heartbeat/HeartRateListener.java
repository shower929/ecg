package com.swm.heartbeat;

import com.swm.core.HeartRateData;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

public interface HeartRateListener {
    void onHeartRateDataAvailable(HeartRateData heartRateData);
}
