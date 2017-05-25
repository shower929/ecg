package com.swm.core;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.crash.FirebaseCrash;
import com.swm.accelerator.AcceleratorListener;
import com.swm.app.superrun.SuperRunActivity;
import com.swm.battery.BatteryListener;
import com.swm.body.PersonalModule;
import com.swm.breath.BreathListener;
import com.swm.device.SwmDeviceListener;
import com.swm.heart.BuildConfig;
import com.swm.heart.R;
import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.RriDistributionListener;
import com.swm.hrv.RriFrequencyListener;
import com.swm.hrv.SdnnListener;
import com.swm.power.PowerModule;
import com.swm.sdk.CaloriePlugin;
import com.swm.sdk.HeartData;
import com.swm.sdk.HeartEngine;
import com.swm.sdk.HeartEngineOutput;
import com.swm.sdk.HeartEngineProvider;
import com.swm.sdk.SwmDevice;
import com.swm.sdk.SwmDeviceModule;
import com.swm.training.TrainingListener;
import com.swm.training.TrainingModule;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service implements HeartEngineOutput {
    private static final String LOG_TAG = "SwmService";
    private static final String CLOSE_APP_ACTION = "close_swm";
    private static final int SWM_NOTIFICATION = 1;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mReceiver;
    private final IBinder mSwmBinder = new SwmBinder();
    SwmDevice device;
    HeartEngine heartEngine;
    private CaloriePlugin caloriePlugin;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "On bind");
        device = SwmDeviceModule.getDevice();

        HeartEngineProvider.init();
        heartEngine = HeartEngineProvider.newEngine(device);

        // Output heart rate to client
        heartEngine.addOutput((HeartEngineOutput) mSwmBinder);

        // Calculate calorie gender, age and weight
        caloriePlugin = new CaloriePlugin(CaloriePlugin.Gender.MALE, 39, 60);
        heartEngine.addOutput(caloriePlugin);

        PowerModule.init(this);
        TrainingModule.init(this);
        PersonalModule.init(this);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SuperRunActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.swm_notification);

        Intent closeIntent = new Intent(CLOSE_APP_ACTION);
        closeIntent.setPackage(getPackageName());
        closeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.swm_close, closePendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.swm_notification)
                .setContent(remoteViews)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(SWM_NOTIFICATION, builder.build());

        IntentFilter filter = new IntentFilter(CLOSE_APP_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mNotificationManager.cancel(SWM_NOTIFICATION);
                stopSelf();
            }
        };
        registerReceiver(mReceiver, filter);

        if(BuildConfig.ENGINEERING) {
            PackageManager pm = getPackageManager();
            ComponentName componentName = new ComponentName(getPackageName(), "com.swm.core.EngineeringActivity");
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }

        return mSwmBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRecording())
            SwmCore.getIns().stop();

        SwmDeviceController.getIns().disconnect();
        unregisterReceiver(mReceiver);

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
        FirebaseCrash.logcat(Log.WARN, LOG_TAG, "Low memory");
    }

    @Override
    public void onHeartDataAvailable(HeartData heartData) {

    }

    /**
     * Created by yangzhenyu on 2016/9/27.
     */

    public class SwmBinder extends Binder {

        SwmBinder() {
        }

        public void setServiceListener(ServiceListener listener) {
            this.listener = listener;
        }

        public void removeMotionListener() {
            SwmCore.getIns().getMotionService().removeListener();
        }


        public void setAcceleratorListener(AcceleratorListener listener) {
            SwmCore.getIns().getAcceleratorService().setListener(listener);
        }

        public void removeAcceleratorListener() {
            SwmCore.getIns().getAcceleratorService().removeListener();
        }

        public void setBreathListener(BreathListener listener) {
            SwmCore.getIns().getBreathService().setListener(listener);
        }

        public void removeBreathListener() {
            SwmCore.getIns().getBreathService().removeListener();
        }

        public void removeHeartRateListener(HeartRateListener listener) {
            SwmCore.getIns().getHeartRateService().removeListener(listener);
        }

        public void setDeviceListener(SwmDeviceListener listener) {
            SwmDeviceController.getIns().setListener(listener);
        }

        public void removeDeviceListener() {
            SwmDeviceController.getIns().removeListener();
        }


        public void logLocation(Location location) {
            if (SwmCore.getIns().getSuperRunCloudService().isMonitoring())
                SwmCore.getIns().getSuperRunCloudService().logLocation(location);
        }

        public void setTrainingListener(TrainingListener listener) {
            SwmCore.getIns().getSportService().setTrainingListener(listener);
        }

        public void removeTrainingListener() {
            SwmCore.getIns().getSportService().removeTrainingListener();
        }

        public void startSport() {
            SwmCore.getIns().getSuperRunCloudService().logEvent(new UserEvent("start_sport"));
        }

        public void stopSport() {
            SwmCore.getIns().getSuperRunCloudService().logEvent(new UserEvent("stop_sport"));
        }

        public void startMonitorHrv() {
            SwmCore.getIns().getHeartRateService().startMonitorHrv();
        }

        public void stopMonitorHrv() {
            SwmCore.getIns().getHeartRateService().stopMonitorHrv();
        }

        public void setRriDistributionListener(RriDistributionListener listener) {
            SwmCore.getIns().getHrvService().setRriDistributionListener(listener);
        }

        public void removeRriDistributionListener() {
            SwmCore.getIns().getHrvService().removeRriDistributionListener();
        }

        public void setRriFreqListener(RriFrequencyListener listener) {
            SwmCore.getIns().getHrvService().setRriFreqListener(listener);
        }

        public void removeRriFreqListener() {
            SwmCore.getIns().getHrvService().removeRriFreqListener();
        }

        public void setSdnnListener(SdnnListener listener) {
            SwmCore.getIns().getHeartRateService().setSdnnListener(listener);
        }

        public void removeSdnnListener() {
            SwmCore.getIns().getHeartRateService().removeSdnnListener();
        }

        public void setRmssdListener(RmssdListener listener) {
            SwmCore.getIns().getHeartRateService().setRmssdListener(listener);
        }

        public void removeRmssdListener() {
            SwmCore.getIns().getHeartRateService().removeRmssdListener();
        }

        public void setBatteryListener(BatteryListener listener) {
            SwmCore.getIns().getBatteryService().setListener(listener);
        }

        public void removeBatteryListener() {
            SwmCore.getIns().getBatteryService().removeListener();
        }
    }
}
