package com.swm.sdk;

import android.content.Context;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class MotionEngineProvider extends GenericMotionEngineProvider{
    public static synchronized void init(Context context) {
        if(MOTION_ENGINE == null)
            MOTION_ENGINE = new MotionEngineImpl(context);
    }
}
