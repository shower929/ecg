package com.swm.sdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by yangzhenyu on 2017/4/29.
 */

abstract class SwmPlugin {
    private final Context context;

    SwmPlugin(Context context) {
        this.context = context;
    }

    void broadcast(String action, String extra, float data) {
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        intent.putExtra(extra, data);

        context.sendBroadcast(intent);
    }

    void broadcast(String action, String extra, int data) {
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        intent.putExtra(extra, data);

        context.sendBroadcast(intent);
    }
}
