package com.swm.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.swm.engineering.app.ServiceListener;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service implements HeartEngineOutput {
    private static final String LOG_TAG = "SwmService";
    private final IBinder mSwmBinder = new SwmBinder();
    private SwmDevice device;
    private HeartEngine heartEngine;
    private ServiceListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "On bind");
        device = SwmDeviceModule.getDevice();

        HeartEngineProvider.init(this);
        heartEngine = HeartEngineProvider.newEngine(device);

        // Output heart rate to client
        heartEngine.addOutput((HeartEngineOutput) mSwmBinder);

        return mSwmBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        heartEngine.stop();
        device.disconnect();
    }

    private boolean isRecording() {
        return SwmCore.getIns().getHeartRateService().isRecording()
                && SwmCore.getIns().getMotionService().isRecording()
                && MyLocationService.isRecording();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onHeartDataAvailable(HeartData heartData) {
        if (mListener != null) {
            if (heartData.hasHeartRate())
                mListener.onHeartRateAvailable(heartData.heartRate);

            // @TODO Wait breath rate algo
            //if(heartData.hasBreathRate())
                //mListener.onBreathRateAvailable(heartData.breathRate);
        }
    }

    /**
     * Created by yangzhenyu on 2016/9/27.
     */

    public class SwmBinder extends Binder {

        SwmBinder() {
        }

        public void setServiceListener(ServiceListener listener) {
            mListener = listener;
        }

        HeartEngine getHeartEngine() {
            return heartEngine;
        }

    }
}
