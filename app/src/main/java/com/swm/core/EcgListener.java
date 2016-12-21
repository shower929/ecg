package com.swm.core;

/**
 * Created by yangzhenyu on 2016/10/29.
 */

interface EcgListener {
    void onEcgDataAvailable(EcgData ecgData);
}
