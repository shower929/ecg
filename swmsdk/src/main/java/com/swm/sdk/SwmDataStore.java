package com.swm.sdk;

import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/5/1.
 */

public class SwmDataStore {
    private List<Double> data;
    private static final int SAMPLE = 250;

    synchronized void putData(double value) {
        if (data == null)
            data = new ArrayList<>();

        data.add(Double.valueOf(value));

    }

    synchronized int read(ByteBuffer dst) {
        if(data == null)
            return 0;

        int size = 0;
        int limit = data.size();
        List<Double> red = new ArrayList<>();
        int bufferCapacity = dst.capacity();

        for(int i = 0; i < limit && (bufferCapacity - dst.position() > 8); i++) {
            Double value = data.get(i);
            red.add(value);

            dst.putDouble(value);
            size++;
        }
        data.removeAll(red);
        return size;
    }
}
