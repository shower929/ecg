package com.swm.breath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.swm.core.BreathData;
import com.swm.heart.R;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/29.
 */

public class BreathView extends SurfaceView {
    private static final String LOG_TAG = "Breath";

    private Paint mBreathPaint;
    private LinkedBlockingQueue<BreathData> mBreathLineBuffer;
    private BreathPlottingWorker mBreathPlotting;

    private int mYShift = 360;
    private double mPxPerValue;
    private int mMaxWidth;
    private int mHeight;
    private Paint mClearPaint;
    private SurfaceHolder mHolder;

    private boolean mDestroyed = false;
    private Bitmap mBitmap;
    private Canvas mBackgroundCanvas;
    private Handler mProfilingHandler;

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mDestroyed = false;
            mHolder = holder;
            mHeight = getHeight();
            mMaxWidth = getWidth();
            mYShift = mHeight / 2;
            mPxPerValue = mHeight / 127;
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mBackgroundCanvas = new Canvas();
            mBackgroundCanvas.setBitmap(mBitmap);
            mBreathPlotting.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mDestroyed = true;
            mBitmap.recycle();
        }
    };

    private class BreathPlottingWorker extends Thread {
        float x1 = 0;
        float y1 = mYShift;

        @Override
        public void run() {
            super.run();
            for(;;) {
                if(mDestroyed) {
                    return;
                }
                try {
                    BreathData breathData = mBreathLineBuffer.take();
                    if (x1 > mMaxWidth) {
                        x1 = 0;
                    }
                    float x2 = x1 + 1;
                    float y2 = (float) (breathData.breathRate * mPxPerValue + mYShift + getTop());
                    mBackgroundCanvas.drawRect(x1, getTop(), x1+15, getTop() + getHeight(), mClearPaint);
                    mBackgroundCanvas.drawLine(x1, y1, x2, y2, mBreathPaint);
                    x1 = x2;
                    y1 = y2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Canvas canvas = mHolder.lockCanvas();
                canvas.drawBitmap(mBitmap, 0, 0, null);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    public BreathView(Context context) {
        this(context, null);
    }

    public BreathView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BreathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(mSurfaceHolderCallback);
        init();
    }

    private void init() {

        mBreathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBreathPaint.setColor(getResources().getColor(R.color.swm_breath_line_color));
        mBreathPaint.setStrokeWidth(5);

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.BLACK);

        mBreathLineBuffer = new LinkedBlockingQueue<>();

        mBreathPlotting = new BreathPlottingWorker();

        mProfilingHandler = new Handler();
        mProfilingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "Breath rendering buffer: " + mBreathLineBuffer.size());
                mProfilingHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public void putBreathData(BreathData breathData) {
        mBreathLineBuffer.offer(breathData);
    }
}
