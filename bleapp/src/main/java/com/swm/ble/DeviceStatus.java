package com.swm.ble;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yangzhenyu on 2017/3/15.
 */

public class DeviceStatus extends View {

    public DeviceStatus(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DeviceStatus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DeviceStatus(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void active() {
        setBackground(getResources().getDrawable(R.drawable.ble_status_active, null));
    }

    public void inactive() {
        setBackground(getResources().getDrawable(R.drawable.ble_status_inactive, null));
    }
}
