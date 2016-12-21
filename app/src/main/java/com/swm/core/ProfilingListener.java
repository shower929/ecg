package com.swm.core;

/**
 * Created by yangzhenyu on 2016/12/2.
 */

interface ProfilingListener {
    void onPacketLoss(double rate);
    void onThroughput(double bps);
    void onByteError(double ber);
}
