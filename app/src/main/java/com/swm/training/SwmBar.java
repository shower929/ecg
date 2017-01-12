package com.swm.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.swm.heart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2016/10/20.
 */

public class SwmBar extends View {
    private static int TYPE_METER = 1;
    private static int TYPE_BALANCER = 2;

    private float mOuterXRadius;
    private float mOuterYRadius;
    private float mInnerXRadius;
    private float mInnerYRadius;
    private float mInnerWidth;
    private RectF mOuterRect;
    private RectF mInnerRect;
    private Paint mOuterPaint;
    private Paint mInnerPaint;
    private Paint mValuePaint;

    private RectF mMiddleRect;
    private Paint mMiddlePaint;
    private float mMiddleXRadius;
    private float mMiddleYRadius;

    private int mOuterColor;
    private float mTextSize;
    private int mType;
    private List<Float> mValueBuffer;
    private static final int MARGIN = 20;
    private int mInnerHeight;
    private int mMax = 10000;
    private float mBalanceValue = -1f;

    public SwmBar(Context context) {
        this(context, null);
    }

    public SwmBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwmBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwmBar, 0, 0);
        try {
            mOuterColor = a.getColor(R.styleable.SwmBar_backgroundColor, 0x5b000000);
            mInnerWidth = a.getDimension(R.styleable.SwmBar_innerWidth, 20);
            mType = a.getInt(R.styleable.SwmBar_type, TYPE_METER);
            mTextSize = a.getDimension(R.styleable.SwmBar_textSize, 20);
        } finally {
            a.recycle();
        }
        mInnerPaint = new Paint();
        mInnerPaint.setColor(getResources().getColor(R.color.swm_intensity_easy));
        mInnerPaint.setAntiAlias(true);

        mValuePaint = new Paint();
        mValuePaint.setColor(getResources().getColor(R.color.swm_intensity_easy));
        mValuePaint.setAntiAlias(true);
        mValuePaint.setTextSize(mTextSize);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mOuterPaint = new Paint();
        mOuterPaint.setColor(mOuterColor);
        mOuterPaint.setAntiAlias(true);

        initBalancer();

        mValueBuffer = new ArrayList();
    }

    private void initBalancer() {
        if (mType != TYPE_BALANCER)
            return;

        mMiddlePaint = new Paint();
        mMiddlePaint.setColor(getResources().getColor(R.color.swm_bar_middle));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mInnerHeight = bottom - top - MARGIN * 2;
        mOuterRect = new RectF(0, 0, right - left, bottom - top);
        mInnerXRadius = (right - left) / 2;
        mInnerYRadius = (right - left) / 2;
        mOuterXRadius = (right - left) / 2;
        mOuterYRadius = (right - left) / 2;
        layoutValueBar(left, top, right, bottom);
    }

    private void layoutValueBar(int left, int top, int right, int bottom) {
        int width = right - left;
        int midX = width / 2;
        int midY = (bottom - top) / 2;

        if (mType == TYPE_BALANCER) {
            mMiddleXRadius = width / 2;
            mMiddleYRadius = width / 2;
            mMiddleRect = new RectF(midX - width / 2 + 3, midY - 10, midX + width / 2 - 3, midY + 10);
            mInnerRect = new RectF(midX - mInnerWidth/2, midY - 30, midX + mInnerWidth / 2, midY + 30);
        } else {
            mInnerRect = new RectF(midX-mInnerWidth/2, bottom - top - 20, midX + mInnerWidth/2, bottom - top - 20);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void drawCustomize(Canvas canvas) {
        if (mType != TYPE_BALANCER)
            return;

        canvas.drawRoundRect(mMiddleRect, mMiddleXRadius, mMiddleYRadius, mMiddlePaint);
    }

    private void drawBalancer(Float value, Canvas canvas) {

        float newMid = mInnerRect.top + (mInnerRect.bottom - mInnerRect.top) / 2;
        if (mBalanceValue != -1) {
            float diff = value - mBalanceValue;
            newMid = newMid + diff;
        }

        mInnerRect.top = newMid - 30;
        mInnerRect.bottom = newMid + 30;
        canvas.drawRoundRect(mInnerRect, mInnerXRadius, mInnerYRadius, mInnerPaint);
    }

    private void drawBar(Float value, Canvas canvas) {
        if(mType == TYPE_BALANCER)
            drawBalancer(value, canvas);
        else
            drawMeter(value, canvas);
    }

    private void drawMeter(Float value, Canvas canvas) {
        float height = value * mInnerHeight / mMax;
        if (height >= mInnerHeight)
            height = mInnerHeight;

        if (height <= 0)
            return;

        float top = mInnerRect.bottom - height;
        mInnerRect.top = top;

        canvas.drawRoundRect(mInnerRect, mInnerXRadius, mInnerYRadius, mInnerPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(mOuterRect, mOuterXRadius, mOuterYRadius, mOuterPaint);
        drawCustomize(canvas);

        synchronized (mValueBuffer) {
            for (Float value : mValueBuffer) {
                drawBar(value, canvas);
            }

            if(mValueBuffer.size() == 0)
                return;

            canvas.drawText(String.valueOf(mValueBuffer.get(mValueBuffer.size() - 1).intValue()), canvas.getWidth()/2, mInnerRect.top - 10, mValuePaint);
            canvas.drawRoundRect(mInnerRect, mInnerXRadius, mInnerYRadius, mInnerPaint);
            mValueBuffer.clear();
        }
    }

    public void setValue(float value) {
        synchronized (mValueBuffer) {
            mValueBuffer.add(Float.valueOf(value));
        }
    }

    public void setMaxValue(int max) {
        mMax = max;
    }

    public void setBalanceValue(int balanceValue) {
        mBalanceValue = balanceValue;
    }
}
