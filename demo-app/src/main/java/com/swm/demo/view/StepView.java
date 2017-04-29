package com.swm.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.demo.R;

/**
 * Created by yangzhenyu on 2017/4/27.
 */

public class StepView extends AppCompatTextView {
    public StepView(Context context) {
        super(context);
    }

    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setStep(final int step) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(getResources().getString(R.string.swm_step, step));
            }
        });
    }
}
