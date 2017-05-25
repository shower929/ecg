package com.swm.motion;

import com.swm.sdk.MotionData;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public interface MotionListener {
    void onMotionDataAvailable(MotionData motionData);
}
