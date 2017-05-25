package com.swm.core;

import com.swm.sdk.EcgData;

/**
 * Created by yangzhenyu on 2016/10/30.
 */

public interface SwmCloudEcgListener {
    void onDataAvailable(EcgData ecgData);
}
