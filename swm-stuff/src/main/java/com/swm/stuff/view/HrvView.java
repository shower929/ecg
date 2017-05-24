package com.swm.stuff.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by yangzhenyu on 2017/4/29.
 */

public class HrvView extends SwmTextView {
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

    public void setSdnn(final float sdnn) {
        this.sdnn = sdnn;

        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format(getFormatString(), sdnn, rmssd));
            }
        });

    }

    public void setRmssd(final float rmssd) {
        this.rmssd = rmssd;

        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format(getFormatString(), sdnn, rmssd));
            }
        });

    }
}
