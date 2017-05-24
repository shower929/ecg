package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

abstract class SwmEngine implements GenericEngine {

    abstract void onFuel(BleData data);

}
