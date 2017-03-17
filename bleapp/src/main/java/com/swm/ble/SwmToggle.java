package com.swm.ble;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * Created by yangzhenyu on 2017/3/15.
 */

public class SwmToggle extends ToggleButton {
    public SwmToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwmToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwmToggle(Context context) {
        super(context);
    }

    public SwmToggle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void on() {
        post(new Runnable() {
            @Override
            public void run() {
                setChecked(true);
            }
        });
    }

    public void off() {
        post(new Runnable() {
            @Override
            public void run() {
                setChecked(false);
            }
        });
    }
}
