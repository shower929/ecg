package com.swm.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yangzhenyu on 2016/12/6.
 */

class GyroView extends GLSurfaceView {

    public GyroView(Context context) {
        this(context, null);
    }

    public GyroView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
