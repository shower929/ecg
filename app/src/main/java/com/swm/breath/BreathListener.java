package com.swm.breath;

import com.swm.core.BreathData;

/**
 * Created by yangzhenyu on 2016/10/2.
 */

public interface BreathListener {
    void onBreathDataAvailable(BreathData breathData);
}
