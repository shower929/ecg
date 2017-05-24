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
    public static final String ACTION_HRV = "action_hrv";
    public static final String EXTRA_HEART_RATE = "extra_heart_rate";
    public static final String EXTRA_SDNN = "extra_sdnn";
    public static final String EXTRA_RMSSD = "extra_rmssd";

    private Dump<HeartData> mDump;
    private Dump<RawEcg> rawDump;

    protected List<HeartEngineOutput> outputs;

    private volatile boolean logging;

    protected SwmEcgMonitor monitor;

    @NonNull
    private final Context context;

    protected HeartEngine(Context context) {
        this.context = context;
    }

    public synchronized void log() {
        logging = true;
    }

    public synchronized void nolog() {
        logging = false;
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
        intent.putExtra(EXTRA_HEART_RATE, heartRate);
        context.sendBroadcast(intent);
    }

    protected void sendHrvBroadcast(float sdnn, float rmssd) {
        Intent intent = new Intent(ACTION_HRV);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_SDNN, sdnn);
        intent.putExtra(EXTRA_RMSSD, rmssd);
        context.sendBroadcast(intent);
    }

    @Override
    void onFuel(BleData data) {

        if (logging)
            rawDump.putData(new RawEcg(data.rawData));
    }

    @Override
    public void start() {
        mDump = new Dump<>("HeartData");
        mDump.start();
        rawDump = new Dump<>("RawEcg");
        rawDump.setWithoutComma(true);
        rawDump.start();
    }

    @Override
    public void stop() {
        if (mDump != null) {
            mDump.stop();
            mDump = null;
        }

        if (rawDump != null) {
            rawDump.stop();
            rawDump = null;
        }
    }

    protected void logHeartData(HeartData heartData) {
        mDump.putData(heartData);
    }

    public void setListener(SwmEngineListener listener) {
        if (monitor == null)
            throw new RuntimeException("No monitor");

        monitor.setListener(listener);
    }
}
