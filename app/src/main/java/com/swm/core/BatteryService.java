package com.swm.core;

import com.swm.battery.BatteryListener;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class BatteryService implements SwmDataListener{
    private BatteryListener mListener;

    @Override
    public synchronized void onSwmDataAvailable(SwmData swmData) {
        if (mListener == null)
            return;
        Integer percent = new Integer(swmData.value[0]);
        mListener.onBatteryDataAvailable(new BatteryData(percent));
    }

    synchronized void setListener(BatteryListener listener) {
        mListener = listener;
    }

    synchronized void removeListener() {
        mListener = null;
    }
}
