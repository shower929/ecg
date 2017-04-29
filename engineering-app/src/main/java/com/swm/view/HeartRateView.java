package com.swm.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.heart.R;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class HeartRateView extends AppCompatTextView {
    private int maxHeartRate = Integer.MIN_VALUE;
    private int minHeartRate = Integer.MAX_VALUE;

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
        post(new Runnable() {
            @Override
            public void run() {
                if (heartRate > maxHeartRate)
                    maxHeartRate = heartRate;

                if (heartRate < minHeartRate)
                    minHeartRate = heartRate;

                setText(getResources().getString(R.string.swm_my_heart_rate, heartRate, maxHeartRate, minHeartRate));
            }
        });
    }
}
