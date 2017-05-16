package com.swm.engineering;

import android.app.Activity;
import android.app.Application;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.swm.chart.Oscilloscope;
import com.swm.chart.delegate.OscilloscopeDelegate;
import com.swm.filter.SwmFilter;
import com.swm.sdk.EcgData;
import com.swm.sdk.SwmEngineListener;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Created by yangzhenyu on 2017/5/10.
 */

public class OscilloscopeController implements Application.ActivityLifecycleCallbacks
                                                , OscilloscopeDelegate
                                                , SwmEngineListener {
    private final Oscilloscope oscilloscope;

    private Activity myActivity;

    private Deque<SwmFilter> filters;

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;
    private float scale = 1.0f;
    private int count = 0;
    private volatile boolean surfaceDestroyed = false;

    private void doScale() {

        double amplitude = max - min;

        // Wave is not out side of window;
        if(amplitude < oscilloscope.getHeight())
            return;

        scale = (float) (oscilloscope.getHeight() / amplitude);

        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;

    }

    public OscilloscopeController(Oscilloscope oscilloscope) {
        this.oscilloscope = oscilloscope;
        oscilloscope.setDelegate(this);
        filters = new ArrayDeque<>();
    }

    public void setActivity(Activity activity) {
        myActivity = activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(myActivity == null)
            return;

        if(activity != myActivity)
            return;

        start();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (myActivity == null)
            return;

        if(activity != myActivity)
            return;

        stop();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void stop() {
        oscilloscope.setLayerType(View.LAYER_TYPE_NONE, null);
        oscilloscope.setVisibility(View.GONE);
    }

    public void start() {
        oscilloscope.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        oscilloscope.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEcgDataAvailable(EcgData data) {
        if (surfaceDestroyed)
            return;

        filter(data);

        int length = data.samples.length;

        for(int i = 0; i < length; i++)
            data.samples[i] = data.samples[i] * scale;

        oscilloscope.plotting(data.samples);
        oscilloscope.postInvalidate();

        count += length;

        if(count == 1500) {
            doScale();
            count = 0;
        }

    }

    private void filter(EcgData data) {
        for (SwmFilter filter: filters)
            filter.filter(data);
    }

    public void addFilter(SwmFilter filter) {
        synchronized (filters) {
            filters.add(filter);
        }
    }
}
