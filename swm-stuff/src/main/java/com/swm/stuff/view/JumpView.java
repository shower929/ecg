package com.swm.stuff.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by yangzhenyu on 2017/5/16.
 */

public class JumpView extends AppCompatTextView{
    public JumpView(Context context) {
        this(context, null);
    }

    public JumpView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public JumpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setStatus(final String status) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(status);
            }
        });
    }
}
