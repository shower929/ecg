package com.swm.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class CalorieView extends AppCompatTextView {
    public CalorieView(Context context) {
        super(context);
    }

    public CalorieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalorieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCalorie(final float calorie) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.valueOf(calorie));
            }
        });
    }
}
