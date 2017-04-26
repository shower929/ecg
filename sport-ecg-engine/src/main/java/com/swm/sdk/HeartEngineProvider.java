package com.swm.sdk;

import android.content.Context;

/**
 * Created by yangzhenyu on 2017/4/19.
 */

public class HeartEngineProvider extends GenericHeartEngineProvider {

    public static synchronized void init(Context context) {

        if(HEART_ENGINE == null)
            HEART_ENGINE = new SportHeartEngine(context);

    }
}
