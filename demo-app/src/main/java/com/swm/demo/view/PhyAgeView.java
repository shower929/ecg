package com.swm.demo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.demo.R;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class PhyAgeView extends AppCompatTextView {
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
                setText(getResources().getString(R.string.swm_phy_age, phyAge));
            }
        });
    }
}
