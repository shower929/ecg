package com.swm.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.swm.engineering.ServiceListener;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service {
    private static final String LOG_TAG = "SwmService";
    private final IBinder mSwmBinder = new SwmBinder();
    private SwmDevice device;
    private HeartEngine heartEngine;
    private MotionEngine motionEngine;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "On bind");
        device = SwmDeviceModule.getDevice();

        HeartEngineProvider.init(this);
        heartEngine = HeartEngineProvider.newEngine(device);

        MotionEngineProvider.init();
        motionEngine = MotionEngineProvider.newEngine(device);
        return mSwmBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        heartEngine.stop();
        device.disconnect();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Created by yangzhenyu on 2016/9/27.
     */

    public class SwmBinder extends Binder {

        SwmBinder() {
        }

        public HeartEngine getHeartEngine() {
            return heartEngine;
        }

        public MotionEngine getMotionEngine() {
            return motionEngine;
        }

    }
}
