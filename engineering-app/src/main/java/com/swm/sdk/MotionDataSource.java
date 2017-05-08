package com.swm.sdk;

import com.swm.chart.LineDataSource;

import java.nio.DoubleBuffer;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public abstract class MotionDataSource  {

    abstract public LineDataSource getXSource();

    abstract public LineDataSource getYSource();

    abstract public LineDataSource getZSource();

}
