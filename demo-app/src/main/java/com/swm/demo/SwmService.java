package com.swm.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.swm.sdk.CaloriePlugin;
import com.swm.sdk.HeartEngine;
import com.swm.sdk.HeartEngineProvider;
import com.swm.sdk.HrvPlugin;
import com.swm.sdk.MotionEngine;
import com.swm.sdk.MotionEngineProvider;
import com.swm.sdk.RunningPlugin;
import com.swm.sdk.SwmDevice;
import com.swm.sdk.SwmDeviceModule;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service {
    private static final String LOG_TAG = "SwmService";
    public static final String CLOSE_APP_ACTION = "com.swm.heart.CLOSE_APP";

    private static final int SWM_NOTIFICATION = 1;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mReceiver;
    private final IBinder mSwmBinder = new SwmBinder();
    private SwmDevice device;
    private HeartEngine heartEngine;
    private CaloriePlugin caloriePlugin;
    private HrvPlugin hrvPlugin;

    private int gender;
    private int age;
    private int weight;

    private MotionEngine motionEngine;
    private RunningPlugin runningPlugin;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "On bind");
        device = SwmDeviceModule.getDevice();
        HeartEngineProvider.init(this);
        heartEngine = HeartEngineProvider.newEngine(device);

        MotionEngineProvider.init();
        motionEngine = MotionEngineProvider.newEngine(device);

        runningPlugin = new RunningPlugin(this);
        motionEngine.setOutput(runningPlugin);

        motionEngine.start();

        SharedPreferences pref = getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        gender = CaloriePlugin.MALE;//pref.getInt(SwmPref.SWM_GENDER, 0);
        age = 39;//pref.getInt(SwmPref.SWM_AGE, 0);
        weight = 60;//pref.getInt(SwmPref.SWM_WEIGHT, 0);

        // Calculate calorie gender, age and weight
        caloriePlugin = new CaloriePlugin(this, gender, age, weight);
        //@TODO Add VO2Max to calorie calculation
        //runningPlugin.addListener(caloriePlugin);
        heartEngine.addOutput(caloriePlugin);

        caloriePlugin.on();

        // Hrv plugin
        hrvPlugin = new HrvPlugin(age);

        // HRV plugin processing heart rate
        heartEngine.addOutput(hrvPlugin);

        // Turn on HRV plugin
        hrvPlugin.on();

        // Start heart engine
        heartEngine.start();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
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
        return mSwmBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        heartEngine.stop();
        device.disconnect();
        unregisterReceiver(mReceiver);

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
    }
}
