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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.swm.app.superrun.SuperRunActivity;
import com.swm.body.PersonalModule;
import com.swm.heart.BuildConfig;
import com.swm.heart.R;
import com.swm.power.PowerModule;
import com.swm.training.TrainingModule;

import static com.swm.heart.SwmApplication.CLOSE_APP_ACTION;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

public class SwmService extends Service {
    private static final String LOG_TAG = "SwmService";
    private static final int SWM_NOTIFICATION = 1;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mReceiver;
    private final IBinder mSwmBinder = new SwmBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(!SwmCore.getIns().getSuperRunCloudService().isMonitoring())
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                SwmCore.getIns().getSuperRunCloudService().startMonitor(FirebaseAuth.getInstance().getCurrentUser());
        return mSwmBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.d(LOG_TAG, "On start command");
        SwmCore.init(getApplicationContext());

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

        return START_STICKY;
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
        return SwmCore.getIns().getEcgService().isRecording()
                && SwmCore.getIns().getHeartRateService().isRecording()
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
}
