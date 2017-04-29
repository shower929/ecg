package com.swm.sdk;

import android.content.Context;

/**
 * Created by yangzhenyu on 2017/3/28.
 */

public class HeartEngineProvider extends GenericHeartEngineProvider {

    public synchronized static final void init(Context context) {
        checkPermission(context);
        if (HEART_ENGINE == null)
            HEART_ENGINE = new MockHeartEngine();
    }
}
