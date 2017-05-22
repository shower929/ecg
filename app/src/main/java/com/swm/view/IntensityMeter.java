package com.swm.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.swm.app.superrun.power.MeterListener;
import com.swm.app.superrun.power.SwmMeter;
import com.swm.heart.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yangzhenyu on 2016/10/11.
 */

public class IntensityMeter extends View {

    private Paint mInnerPaint;
    private Paint mDefaultPaint;

    private List<Float> mBuffer;
    private RectF mOuterBase;
    private RectF mInnerBase;
    private RectF mSecondBase;
    private float mInnerCircleX;
    private float mInnerCircleY;
    private float mInnerWidth;
    private float mInnerRadius;

    private float mOuterRadius;
    private Paint mOuterPaint;
    private float mOuterWidth;

    private float mSecondX;
    private float mSecondY;
    private float mSecondCircleWidth;
    private float mSecondCircleRadius;
    private Paint mSecondCirclePaint;
    private float mSecondCircleValueSize;
    private float mSecondCircleTextSize;
    private Paint mSecondCircleValuePaint;
    private Paint mSecondCircleTextPaint;
    private String mSecondCircleText;
    private String mSecondValue = "00";

    private int[] mSweepColors;
    private Paint mOuterSweepPaint;

    private Animator mRunPowerColorAnim;
    private int mPowerColor;
    private float mOuterStart = 91;
    public final static int MAX_DEGREE = 300;
    private Bitmap mPointer;

    private float mPointerLeft;
    private float mPointerTop;

    private int excellentLevel;
    private int goodLevel;
    private int poorLevel;

    private int max;
    private float currentValue;
    private int mMeterValueColor;
    private Animator mMeterValueColorAnim;
    private volatile boolean init;
    private long previous;
    private static final int FPS = 60;
    private OvershootInterpolator overshootInterpolator;

    private static final ReentrantLock LOCK = new ReentrantLock();

    public IntensityMeter(Context context) {
        this(context, null);
    }

    public IntensityMeter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntensityMeter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBuffer = new ArrayList<>();
        mInnerPaint = new Paint();
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setAntiAlias(true);

        mDefaultPaint = new Paint();
        mDefaultPaint.setStyle(Paint.Style.STROKE);
        mDefaultPaint.setAntiAlias(true);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setAntiAlias(true);

        mSecondCirclePaint = new Paint();
        mSecondCirclePaint.setStyle(Paint.Style.STROKE);
        mSecondCirclePaint.setAntiAlias(true);
        mSecondCircleValuePaint = new Paint();
        mSecondCircleValuePaint.setAntiAlias(true);
        mSecondCircleValuePaint.setColor(getResources().getColor(R.color.swm_white));
        mSecondCircleValuePaint.setTextAlign(Paint.Align.CENTER);
        mSecondCircleTextPaint = new Paint();
        mSecondCircleTextPaint.setAntiAlias(true);
        mSecondCircleTextPaint.setColor(getResources().getColor(R.color.swm_white));
        mSecondCircleTextPaint.setTextAlign(Paint.Align.CENTER);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwmMeter,
                0,
                0
        );

        try {
            mInnerWidth = a.getDimension(R.styleable.SwmMeter_innerCircleWidth, 5);
            mInnerPaint.setStrokeWidth(mInnerWidth);
            mInnerPaint.setColor(a.getColor(R.styleable.SwmMeter_innerCircleColor, 0xffffff));
            mInnerRadius = a.getDimension(R.styleable.SwmMeter_innerCircleRadius, 50);

            mOuterWidth = a.getDimension(R.styleable.SwmMeter_outerCircleWidth, 5);
            mOuterPaint.setStrokeWidth(mOuterWidth);
            mOuterPaint.setColor(a.getColor(R.styleable.SwmMeter_outerCircleColor, 0xffffff));
            mOuterRadius = a.getDimension(R.styleable.SwmMeter_outerCircleRadius, 50);

            initOuterType(a);

            mDefaultPaint.setStrokeWidth(mOuterWidth);
            mDefaultPaint.setColor(getResources().getColor(R.color.swm_power_default));

            mSecondCircleWidth = a.getDimension(R.styleable.SwmMeter_secondWidth, 5);
            mSecondCirclePaint.setStrokeWidth(mSecondCircleWidth);
            mSecondCircleRadius = a.getDimension(R.styleable.SwmMeter_secondRadius, 50);
            mSecondCircleValueSize = a.getDimension(R.styleable.SwmMeter_secondValueSize, 28);
            mSecondCircleTextSize = a.getDimension(R.styleable.SwmMeter_secondTextSize, 24);
            mSecondCircleValuePaint.setTextSize(mSecondCircleValueSize);
            mSecondCircleTextPaint.setTextSize(mSecondCircleTextSize);
            mSecondCircleText = a.getString(R.styleable.SwmMeter_secondText);
            mPointer = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.SwmMeter_pointer, R.drawable.swm_arrow));

        } finally {
            a.recycle();
        }

        overshootInterpolator = new OvershootInterpolator(0.2f);
    }

    private void initOuterType(TypedArray a) {
        mOuterSweepPaint = new Paint();
        mOuterSweepPaint.setStyle(Paint.Style.STROKE);
        mOuterSweepPaint.setStrokeWidth(mOuterWidth);
        mOuterSweepPaint.setAntiAlias(true);
        int sweepColorRes = a.getResourceId(R.styleable.SwmMeter_sweepColors, 0);
        if (sweepColorRes != 0)
            mSweepColors = getResources().getIntArray(sweepColorRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mInnerCircleX = (right - left) / 2;
        mInnerCircleY = top + mOuterRadius + mOuterWidth;
        mInnerBase = new RectF();
        mInnerBase.left = mInnerCircleX - mInnerRadius;
        mInnerBase.top = mInnerCircleY - mInnerRadius;
        mInnerBase.right = mInnerCircleX + mInnerRadius;
        mInnerBase.bottom = mInnerCircleY + mInnerRadius;

        mOuterBase = new RectF();
        mOuterBase.left = mInnerCircleX - mOuterRadius;
        mOuterBase.top = mInnerCircleY - mOuterRadius;
        mOuterBase.right = mInnerCircleX + mOuterRadius;
        mOuterBase.bottom = mInnerCircleY + mOuterRadius;

        applyOuterType();

        mSecondX = mInnerCircleX + mInnerRadius - mInnerWidth;
        mSecondY = mInnerCircleY + mOuterRadius + mOuterWidth;
        mSecondBase = new RectF();
        mSecondBase.left = mSecondX - mSecondCircleRadius;
        mSecondBase.top = mSecondY - mSecondCircleRadius;
        mSecondBase.right = mSecondX + mSecondCircleRadius;
        mSecondBase.bottom = mSecondY + mSecondCircleRadius;

        layoutMeter(left, top, right, bottom);
    }

    private void layoutMeter(int left, int top, int right, int bottom) {
        mPointerLeft = mInnerCircleX - mPointer.getWidth() / 2;
        mPointerTop = mInnerCircleY + mInnerRadius - mInnerWidth - 5;
    }

    private void applyOuterType() {
        int len = mSweepColors.length;

        float[] positions = new float[len];
        float position = 1f / len;
        for (int i = 0; i < len; i++) {
            positions[i] = i * position;
        }

        mOuterSweepPaint.setShader(new SweepGradient(mInnerCircleX, mInnerCircleY, mSweepColors, positions));
    }

    private void drawOuter(Canvas canvas) {
        canvas.drawArc(mOuterBase, 91, 300, false, mOuterSweepPaint);
    }

    private void drawMeter(Canvas canvas, float degree) {
        canvas.rotate(degree, mInnerCircleX, mInnerCircleY);
        canvas.drawBitmap(mPointer, mPointerLeft, mPointerTop, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScene(canvas);

        LOCK.lock();
        try {
            if (mBuffer.size() == 0) {
                drawMeter(canvas, toDegree(currentValue));
                return;
            }
            Float value = mBuffer.remove(0);
            Log.d("Shower", "Value: " + value);
            drawMeter(canvas, value);

            if(mBuffer.size() > 0)
                postInvalidate();

        } finally {
            LOCK.unlock();
        }
    }

    private void drawScene(Canvas canvas) {
        canvas.drawArc(mInnerBase, 91, 300, false, mInnerPaint);
        drawOuter(canvas);
        canvas.drawArc(mOuterBase, 91, 300, false, mDefaultPaint);
        canvas.drawArc(mSecondBase, 92, 358, false, mDefaultPaint);
        canvas.drawText(mSecondValue, mSecondX, mSecondY, mSecondCircleValuePaint);
        canvas.drawText(mSecondCircleText, mSecondX, mSecondY + mSecondCircleTextSize + 10, mSecondCircleTextPaint);
    }

    public void on() {
        setDeviceStatusColor(getResources().getColor(R.color.swm_device_status_connected));
    }

    public void off() {
        setDeviceStatusColor(getResources().getColor(R.color.swm_device_status_unconnect));
    }

    private void setDeviceStatusColor(int color) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator animator = ObjectAnimator.ofObject(this, "color", evaluator, mInnerPaint.getColor(), color);
        animator.setDuration(1000);
        animator.start();
    }

    public void setColor(int color) {
        mInnerPaint.setColor(color);
        invalidate();
    }

    public void setSecondValue(final int secondValue) {
        post(new Runnable() {
            @Override
            public void run() {
                mSecondValue = String.valueOf(secondValue);
                invalidate();
            }
        });
    }

    public void updateSecondCircleColor(int color) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator animator = ObjectAnimator.ofObject(this, "secondCircleColor", evaluator, mSecondCirclePaint.getColor(), color);
        animator.setDuration(1000);
        animator.start();
    }

    public void setSecondCircleColor(int color) {
        mSecondCirclePaint.setColor(color);
        invalidate();
    }

    public void setExcellentLevel(int level) {
        excellentLevel = level;
    }

    public void setGoodLevel(int level) {
        goodLevel = level;
    }

    public void setPoorLevel(int level) {
        poorLevel = level;
    }

    public void setValue(final int target) {
        float interpolation = currentValue;
        float base = target - currentValue;
        float step = base / FPS;
        int sign = (target - currentValue) > 0 ? 1 : -1;

        LOCK.lock();

        try {
            for (int i = 0; i < FPS; i++) {
                interpolation  +=  step * sign;
                float degree = toDegree(interpolation);
                mBuffer.add(degree);
            }
        } finally {
            LOCK.unlock();
        }

        currentValue = target;

        invalidate();

    }

    private float toDegree(float digit) {
        return digit * MAX_DEGREE / max;
    }

    public void setMax(int max) {
        this.max = max;
    }


}
