package com.swm.stuff.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.stuff.R;

import static android.R.attr.textViewStyle;

/**
 * Created by yangzhenyu on 2017/5/9.
 */

public class SwmTextView extends AppCompatTextView {
    private String formatString;

    public SwmTextView(Context context) {
        this(context, null);
    }

    public SwmTextView(Context context, AttributeSet attrs) {
        this(context, attrs, textViewStyle);
    }

    public SwmTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwmTextView, 0, 0);
        try {
            formatString = typedArray.getString(R.styleable.SwmTextView_formatString);
        } finally {
            typedArray.recycle();
        }
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public String getFormatString() {
        return formatString;
    }
}
