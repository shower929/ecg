package com.swm.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.demo.R;
import com.swm.sdk.HrvPlugin;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class StressView extends AppCompatTextView {
    public StressView(Context context) {
        super(context);
    }

    public StressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setStress(final HrvPlugin.Stress stress) {
        post(new Runnable() {
            @Override
            public void run() {
                if (stress == HrvPlugin.Stress.BAD)
                    setText(getResources().getString(R.string.swm_stress_bad));
                else if (stress == HrvPlugin.Stress.GOOD)
                    setText(getResources().getString(R.string.swm_stress_good));
                else if (stress == HrvPlugin.Stress.HAPPY)
                    setText(getResources().getString(R.string.swm_stress_happy));
                else if (stress == HrvPlugin.Stress.NORMAL)
                    setText(getResources().getString(R.string.swm_stress_normal));
            }
        });
    }
}
