package com.swm.core;

import com.swm.sdk.EcgRawData;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

interface EcgRawDataListener {
    void onEcgRawDataAvailable(EcgRawData ecgRawData);
}
