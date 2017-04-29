package com.swm.motion;

import android.app.Application;

import com.swm.sdk.SwmDeviceModule;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

public class MotionApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SwmDeviceModule.init(this);
        MotionEngineProvider.init(int);
        MotionEngine engine = MotionEngineProvider.get();
    }
}
