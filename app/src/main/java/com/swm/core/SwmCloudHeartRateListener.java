package com.swm.core;

import com.swm.sdk.HeartRateData;

/**
 * Created by yangzhenyu on 2016/11/1.
 */

public interface SwmCloudHeartRateListener {
    void onDataAvailable(HeartRateData heartRateData);
}
