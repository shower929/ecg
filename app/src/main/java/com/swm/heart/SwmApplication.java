package com.swm.heart;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.database.FirebaseDatabase;
import com.swm.app.superrun.SuperRunActivity;
import com.swm.body.PersonalModule;
import com.swm.core.SwmService;
import com.swm.core.SwmDeviceController;
import com.swm.power.PowerModule;
import com.swm.training.TrainingModule;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public class SwmApplication extends Application {

    public static final String CLOSE_APP_ACTION = "com.swm.heart.CLOSE_APP";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
