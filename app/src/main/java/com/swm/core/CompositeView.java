package com.swm.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.swm.heart.BuildConfig;
import com.swm.heart.R;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/29.
 */

public class CompositeView extends SurfaceView {
    private static final String LOG_TAG = "ECG";

    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundDrawing;

    private Paint mEcgPaint;
    private LinkedBlockingQueue<EcgData> mEcgLineBuffer;
    private EcgPlottingWorker mEcgPlotting;

    private float mEcgZeroShift = 360;
    private float mEcgHeight;
    private float mEcgTop;
    private float mEcgBottom;

    private static final int GAP = 3;
    private int mMaxWidth;
    private int mHeight;
    private Paint mClearPaint;
    private SurfaceHolder mHolder;

    private boolean mRendering = false;

    private Handler mProfilingHandler;
    private Runnable mProfiling;
    private float mDensity;
    private float mPilotSize;
    private static final int PILOT_SIZE = 40;//dp
    private static final int LINE_SIZE = 3;//dp

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.v("drawing", "surfaceCreated");
            mHolder = holder;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = false;
            if (Build.VERSION.SDK_INT < 21)
                isScreenOn = pm.isScreenOn();
            else
                isScreenOn = pm.isInteractive();

            Log.v("drawing", "surfaceChanged: " + isScreenOn);
            if(!isScreenOn)
                stopDrawing();
            else
                startDrawing();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v("drawing", "surfaceDestroyed");
            stopDrawing();
        }
    };

    private class EcgPlottingWorker extends Thread {
        int x1=0;
        float y1 = mEcgZeroShift;
        float mScale = 1;
        float mAmp = 1;
        float mDc = 0;
        float mThreshold = 0;
        float mLowestValue = Integer.MAX_VALUE;
        float mHighestValue = Integer.MIN_VALUE;

        @Override
        public void run() {
            Log.v(LOG_TAG, "ECG plotting worker is running");
            super.run();
            try {

                for (;;) {
                    if (!mRendering) {
                        Log.v(LOG_TAG, "ECG stop rendering");
                        return;
                    }

                    EcgData ecgData = mEcgLineBuffer.take();
                    if (x1 >= mMaxWidth) {
                        x1 = 0;
                    }

                    if (mBackgroundBitmap == null) {
                        mBackgroundBitmap = Bitmap.createBitmap(mMaxWidth, mHeight, Bitmap.Config.ARGB_8888);
                        mBackgroundDrawing = new Canvas();
                        mBackgroundDrawing.setBitmap(mBackgroundBitmap);
                    }

                    for (int value : ecgData.values) {
                        if (x1 % 250 == 0) {
                            scale();
                        }

                        float y2 = (-value * mScale + mEcgZeroShift);

                        if (value < mLowestValue)
                            mLowestValue = value;

                        if (value > mHighestValue)
                            mHighestValue = value;

                        int x2 = (x1+1);
                        mBackgroundDrawing.drawRect(x1, mEcgTop-GAP-2, x1+mPilotSize, mEcgBottom+GAP+2, mClearPaint);
                        mBackgroundDrawing.drawLine(x1, y1, x2, y2, mEcgPaint);
                        x1 = x2;
                        y1 = y2;
                    }

                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            } catch (InterruptedException e) {
                Log.v(LOG_TAG, "ECG plotting stop");
            }
        }

        private void rebase() {
            if (mHighestValue - mLowestValue == 0)
                return;

            mDc = (mHighestValue - mLowestValue) / 2 - mEcgZeroShift;
        }

        private void scale() {
            if(Math.abs(mHighestValue - mLowestValue) == mAmp)
                return;

            if (Math.abs(mHighestValue - mLowestValue) < 100)
                return;

            mAmp = Math.abs(mHighestValue - mLowestValue);
            mScale = mEcgHeight / mAmp * 0.7f;
            // Reset high/low value to research highest / lowest value
            mHighestValue = Integer.MIN_VALUE;  // Initial highest value, should be smallest value compare to real signal
            mLowestValue = Integer.MAX_VALUE;   // Initial lowest value, should be greatest value compare to real singal
        }
    }

    public CompositeView(Context context) {
        this(context, null);
    }

    public CompositeView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CompositeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(mSurfaceHolderCallback);
        init();

    }

    private void init() {

        mDensity = getResources().getDisplayMetrics().density;
        mPilotSize = PILOT_SIZE * mDensity;

        mEcgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEcgPaint.setColor(getResources().getColor(R.color.swm_ecg_color));
        mEcgPaint.setStrokeWidth(LINE_SIZE * mDensity);

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.BLACK);

        mEcgLineBuffer = new LinkedBlockingQueue<>();

        mProfilingHandler = new Handler();

    }

    public synchronized void putEcgData(EcgData ecgData) {
        if (mRendering)
            mEcgLineBuffer.offer(ecgData);
    }

    private synchronized void stopDrawing() {
        if(!mRendering)
            return;

        mRendering = false;
        if (mEcgPlotting != null) {
            mEcgPlotting.interrupt();
        }

        if (mProfiling != null) {
            mProfilingHandler.removeCallbacks(mProfiling);
        }

        if (mBackgroundBitmap != null) {
            mBackgroundBitmap.recycle();
            mBackgroundBitmap = null;
        }
    }

    private synchronized void startDrawing() {
        if(mRendering)
            return;

        mRendering = true;
        mEcgPlotting = new EcgPlottingWorker();
        mEcgPlotting.start();
        mProfiling = new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "ECG rendering buffer: " + mEcgLineBuffer.size());
                mProfilingHandler.postDelayed(this, 1000);
            }
        };
        mProfilingHandler.postDelayed(mProfiling, 1000);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = bottom - top;
        mMaxWidth = right - left;

        mEcgHeight = mHeight;
        mEcgTop = top;
        mEcgBottom = mEcgTop + mEcgHeight;
        mEcgZeroShift = mEcgTop + mEcgHeight / 2;

    }

}
