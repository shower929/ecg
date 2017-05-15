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
                                                , SurfaceHolder.Callback
                                                , SwmEngineListener {
    private final Oscilloscope oscilloscope;

    private Activity myActivity;

    private SurfaceHolder surfaceHolder;

    private volatile boolean running;

    private RenderThread renderThread;

    private Deque<SwmFilter> filters;

    private int framePerSecond = 15;

    @Override
    public void scale(double y) {
        /**
         * @TODO Zoom in Zoom out while plot out of range
        for(Double value: buffer) {
            if (value > max)
                max = value;
            if (value < min)
                min = value;
        }

        double amplitude = max - min;

        // Wave is not out side of window;
        if(amplitude < height)
            return;

        scale = (float) (height / amplitude);

        int size = buffer.size();

        for(int i = 0; i < size; i++) {
            double value = buffer.get(i) * scale;
            buffer.set(i, value);
        }
         */
    }

    private class RenderThread extends Thread {

        @Override
        public void run() {
            for(;;) {
                if(!running)
                    return;


                Canvas canvas = surfaceHolder.lockCanvas();

                if (canvas == null)
                    return;

                oscilloscope.drawOnCanvas(canvas);

                surfaceHolder.unlockCanvasAndPost(canvas);
                try {
                    Thread.sleep(1000 / framePerSecond);
                } catch (InterruptedException e) {
                    Log.e("Shower", "User interrupted");
                }
            }
        }

    }

    public OscilloscopeController(Oscilloscope oscilloscope) {
        this.oscilloscope = oscilloscope;
        surfaceHolder = oscilloscope.getHolder();
        surfaceHolder.addCallback(this);
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
        running = false;
        oscilloscope.setVisibility(View.GONE);
        surfaceHolder.removeCallback(this);
    }

    public void start() {
        running = true;
        oscilloscope.setVisibility(View.VISIBLE);
    }

    private void startRender() {
        renderThread = new RenderThread();
        renderThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Shower", "Surfaced created");
        startRender();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Shower", "Surface changed: " + height);
        oscilloscope.initBackground();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("Shower", "Surface destroyed");
        surfaceHolder.lockCanvas();
        running = false;
        renderThread.interrupt();
        oscilloscope.clean();
    }

    @Override
    public void onEcgDataAvailable(EcgData data) {
        filter(data);
        oscilloscope.bufferDrawing(data.samples);
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
