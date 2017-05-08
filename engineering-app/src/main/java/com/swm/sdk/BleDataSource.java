package com.swm.sdk;

import android.support.annotation.Nullable;

import com.swm.chart.LineDataSource;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class BleDataSource extends LineDataSource {

    @Override
    public int read(ByteBuffer buffer) {
        return BleDataStore.getIns().read(buffer);
    }
}
