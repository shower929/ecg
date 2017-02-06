package com.swm.battery;

import com.swm.core.BatteryData;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

public interface BatteryListener {
    void onBatteryDataAvailable(BatteryData batteryData);
}
