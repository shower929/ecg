package com.swm.sdk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

public abstract class HeartEngine extends SwmEngine{
    public static final String ACTION_HEART_RATE = "action_heart_rate";
    public static final String HEART_RATE = "heart_rate";

    List<HeartEngineOutput> outputs;
    HeartEngineRawOutput rawOutput;

    @NonNull
    private final Context context;

    protected HeartEngine(Context context) {
        this.context = context;
    }

    synchronized void setRawOutput(HeartEngineRawOutput rawOutput) {
        this.rawOutput = rawOutput;
    }

    public synchronized void addOutput(HeartEngineOutput output) {
        if(outputs == null)
            outputs = Collections.synchronizedList(new ArrayList<HeartEngineOutput>());

        outputs.add(output);
    }

    synchronized void removeOutput(HeartEngineOutput output) {
        if(outputs == null)
            return;

        outputs.remove(output);
    }

    protected void sendHeartRateBroadcast(int heartRate) {
        Intent intent = new Intent(ACTION_HEART_RATE);
        intent.setPackage(context.getPackageName());
        intent.putExtra(HEART_RATE, heartRate);

        context.sendBroadcast(intent);

    }

}
