package com.swm.stuff.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yangzhenyu on 2017/4/29.
 */

public class HrvView extends AppCompatTextView {
    private CharSequence text;
    private float sdnn;
    private float rmssd;

    public HrvView(Context context) {
        super(context);
    }

    public HrvView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HrvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        this.text = text;
    }

    public void setSdnn(final float sdnn) {
        this.sdnn = sdnn;
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format((String)text, sdnn, rmssd));
            }
        });

    }

    public void setRmssd(final float rmssd) {
        this.rmssd = rmssd;
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format((String)text, sdnn, rmssd));
            }
        });

    }
}
