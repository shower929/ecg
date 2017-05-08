package com.swm.sdk;

import com.swm.chart.LineDataSource;

import java.nio.ByteBuffer;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class MagneticDataSource extends MotionDataSource {
    private MotionDataStoreWrapper motionDataStoreWrapper;

    public MagneticDataSource() {
        try {
            motionDataStoreWrapper = MotionDataStoreWrapper.get(MotionDataStoreWrapper.Type.Magnetic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public LineDataSource getXSource() {
        return new LineDataSource() {
            @Override
            public int read(ByteBuffer buffer) {
                return motionDataStoreWrapper.readX(buffer);
            }
        };
    }

    @Override
    public LineDataSource getYSource() {
        return new LineDataSource() {
            @Override
            public int read(ByteBuffer buffer) {
                return motionDataStoreWrapper.readY(buffer);
            }
        };
    }

    @Override
    public LineDataSource getZSource() {
        return new LineDataSource() {
            @Override
            public int read(ByteBuffer buffer) {
                return motionDataStoreWrapper.readZ(buffer);
            }
        };
    }
}
