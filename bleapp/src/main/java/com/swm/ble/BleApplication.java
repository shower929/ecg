package com.swm.ble;

import android.app.Application;

import com.swm.sdk.SwmClient;
import com.swm.sdk.SwmDeviceModule;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

public class BleApplication extends Application implements SwmClient {
    BleManager ble;

    @Override
    public void onCreate() {
        super.onCreate();
        SwmDeviceModule.init(this);
    }
}
