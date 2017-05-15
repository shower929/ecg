package com.swm.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service {
    private static final String LOG_TAG = "SwmService";
    private final IBinder mSwmBinder = new SwmBinder();
    private SwmDevice device;
    private CaloriePlugin caloriePlugin;
    private HrvPlugin hrvPlugin;
    private HeartEngine heartEngine;
    private MotionEngine motionEngine;
    private RunningPlugin runningPlugin;

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Service", "Unbind");
        heartEngine.stop();
        motionEngine.stop();
        device.disconnect();
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "On bind");
        device = SwmDeviceModule.getDevice();

        HeartEngineProvider.init(this);
        heartEngine = HeartEngineProvider.newEngine(device);

        caloriePlugin = new CaloriePlugin(this, CaloriePlugin.MALE, 39, 60);
        heartEngine.addOutput(caloriePlugin);
        caloriePlugin.on();

        hrvPlugin = new HrvPlugin(this, 39);
        heartEngine.addOutput(hrvPlugin);
        hrvPlugin.on();

        heartEngine.start();

        MotionEngineProvider.init();
        motionEngine = MotionEngineProvider.newEngine(device);

        runningPlugin = new RunningPlugin(this);
        motionEngine.setOutput(runningPlugin);
        runningPlugin.on();

        motionEngine.start();

        return mSwmBinder;
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
