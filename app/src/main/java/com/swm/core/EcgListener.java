package com.swm.core;

import com.swm.sdk.EcgData;

/**
 * Created by yangzhenyu on 2016/10/29.
 */

interface EcgListener {
    void onEcgDataAvailable(EcgData ecgData);
}
