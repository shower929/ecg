package com.swm.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by yangzhenyu on 2017/4/24.
 */

public class HeartRateView extends AppCompatTextView {
    public HeartRateView(Context context) {
        super(context);
    }

    public HeartRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeartRate(final int heartRate) {
        //@TODO Add highlight anim
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.valueOf(heartRate));
            }
        });
    }
}
