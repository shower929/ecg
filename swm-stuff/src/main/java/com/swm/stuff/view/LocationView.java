package com.swm.stuff.view;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class LocationView extends AppCompatTextView {
    private CharSequence text;

    public LocationView(Context context) {
        super(context);
    }

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        this.text = text;
    }

    public void setLocation(final Location location) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(String.format((String)text, location.getLongitude(), location.getLatitude(), location.getAccuracy()));
            }
        });
    }
}
