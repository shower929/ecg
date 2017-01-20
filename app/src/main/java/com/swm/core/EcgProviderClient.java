package com.swm.core;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

public interface EcgProviderClient {
    void onEcgDataAvailable(EcgData ecgData);
}
