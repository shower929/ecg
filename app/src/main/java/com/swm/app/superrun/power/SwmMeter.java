package com.swm.app.superrun.power;

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
import android.view.View;

import com.swm.heart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2016/10/11.
 */

public class SwmMeter extends View {
    private static final String LOG_TAG = "SwmMeter";

    private Paint mInnerPaint;
    private Paint mDefaultPaint;

    private static final Object MAIN_VALUE = new Object();
    private List<Integer> mBuffer;
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


    private int mCurrentPower;
    private MeterListener mMeterListener;
    private Animator mRunPowerColorAnim;
    private int mPowerColor;
    private float mOuterStart = 91;
    public final static int MAX_DEGREE = 300;
    private Bitmap mPointer;
    private int mType;
    private float mPointerLeft;
    private float mPointerTop;

    private int excellentLevel;
    private int goodLevel;
    private int poorLevel;

    public SwmMeter(Context context) {
        this(context, null);
    }

    public SwmMeter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwmMeter(Context context, AttributeSet attrs, int defStyleAttr) {
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
            mType = a.getInt(R.styleable.SwmMeter_meterType, 1);
        } finally {
            a.recycle();
        }
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
        mSecondX = mInnerCircleX + mInnerRadius - mInnerWidth;
        mSecondY = mInnerCircleY + mOuterRadius + mOuterWidth;
        mSecondBase = new RectF();
        mSecondBase.left = mSecondX - mSecondCircleRadius;
        mSecondBase.top = mSecondY - mSecondCircleRadius;
        mSecondBase.right = mSecondX + mSecondCircleRadius;
        mSecondBase.bottom = mSecondY + mSecondCircleRadius;

    }

    public void setRunPower(int power) {
        synchronized (MAIN_VALUE) {
            mBuffer.add(Integer.valueOf(power));
        }
    }

    private void drawMeter(Canvas canvas, int power) {
        if(power >= excellentLevel)
            setRunPowerColor(getResources().getColor(R.color.swm_run_power_excellent));
        else if (power >= goodLevel && power < excellentLevel)
            setRunPowerColor(getResources().getColor(R.color.swm_run_power_good));
        else if (power < goodLevel)
            setRunPowerColor(getResources().getColor(R.color.swm_run_power_poor));

        canvas.drawArc(mOuterBase, 91, power, false, mOuterPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mInnerBase, 91, 300, false, mInnerPaint);
        canvas.drawArc(mOuterBase, 91, 300, false, mDefaultPaint);
        canvas.drawArc(mSecondBase, 92, 358, false, mDefaultPaint);
        canvas.drawText(mSecondValue, mSecondX, mSecondY, mSecondCircleValuePaint);
        canvas.drawText(mSecondCircleText, mSecondX, mSecondY + mSecondCircleTextSize + 10, mSecondCircleTextPaint);

        synchronized (MAIN_VALUE) {
            if (mBuffer.size() == 0) {
                drawMeter(canvas, mCurrentPower);
            } else {

                for(Integer power : mBuffer) {

                    drawMeter(canvas, power);
                    mCurrentPower = power;

                }
                mBuffer.clear();
            }
        }
    }

    private void setRunPowerColor(int color) {
        if (color == mPowerColor)
            return;

        mPowerColor = color;

        if (mRunPowerColorAnim != null && mRunPowerColorAnim.isStarted())
            mRunPowerColorAnim.cancel();

        ArgbEvaluator evaluator = new ArgbEvaluator();
        mRunPowerColorAnim = ObjectAnimator.ofObject(this, "powerColor", evaluator, mOuterPaint.getColor(), color);
        mRunPowerColorAnim.setDuration(500);
        mRunPowerColorAnim.start();
    }

    public void setPowerColor(int color) {
        mOuterPaint.setColor(color);
        invalidate();
    }

    public void turnon() {
        setDeviceStatusColor(getResources().getColor(R.color.swm_device_status_connected));
    }

    public void turnoff() {
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

    public void setCallback(MeterListener listener) {
        this.mMeterListener = listener;
    }

    public void setSecondValue(int secondValue) {
        mSecondValue = String.valueOf(secondValue);
        invalidate();
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
}
