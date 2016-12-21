package com.swm.heartbeat;

import com.swm.core.HeartBeatData;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

public interface HeartBeatListener {
    void onHeartBeatDataAvailable(HeartBeatData heartBeatData);
}
