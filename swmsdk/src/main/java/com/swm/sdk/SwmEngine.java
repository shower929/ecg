package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

interface SwmEngine extends GenericEngine {
    void onFuel(BleData data);
}
