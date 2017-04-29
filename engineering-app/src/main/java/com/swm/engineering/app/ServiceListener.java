package com.swm.engineering.app;

/**
 * Created by yangzhenyu on 2017/4/29.
 */

public interface ServiceListener {
    void onHeartRateAvailable(int heartRate);
    void onBreathRateAvailable(int breathRate);
}
