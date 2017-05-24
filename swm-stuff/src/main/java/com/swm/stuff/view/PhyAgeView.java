package com.swm.stuff.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class PhyAgeView extends SwmTextView {

    public PhyAgeView(Context context) {
        super(context);
    }

    public PhyAgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhyAgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setPhyAge(final int phyAge) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format(getFormatString(), phyAge));
            }
        });
    }
}
