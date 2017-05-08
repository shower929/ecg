package com.swm.sdk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/5/2.
 */

class MotionDataStoreWrapper {
    enum Type {Accelerator, Gyro, Magnetic};

    private static MotionDataStoreWrapper ACCELERATOR_DATA_STORE;
    private static MotionDataStoreWrapper GYRO_DATA_STORE;
    private static MotionDataStoreWrapper MAGNETIC_DATA_STORE;

    private SwmDataStore xDataStore;
    private SwmDataStore yDataStore;
    private SwmDataStore zDataStore;

    private MotionDataStoreWrapper() {
        xDataStore = new SwmDataStore();
        yDataStore = new SwmDataStore();
        zDataStore = new SwmDataStore();
    }

    static synchronized MotionDataStoreWrapper get(Type type) throws Exception {
        if (type == Type.Accelerator) {
            if (ACCELERATOR_DATA_STORE == null)
                ACCELERATOR_DATA_STORE = new MotionDataStoreWrapper();

            return ACCELERATOR_DATA_STORE;
        } else if (type == Type.Gyro) {
            if (GYRO_DATA_STORE == null)
                GYRO_DATA_STORE = new MotionDataStoreWrapper();

            return GYRO_DATA_STORE;
        } else if (type == Type.Magnetic) {
            if (MAGNETIC_DATA_STORE == null)
                MAGNETIC_DATA_STORE = new MotionDataStoreWrapper();

            return MAGNETIC_DATA_STORE;
        }
        throw new Exception("No such sensor: " + type);
    }

    synchronized int readX(ByteBuffer dst) {
        if(xDataStore == null)
            return 0;

        return xDataStore.read(dst);
    }

    synchronized int readY(ByteBuffer dst) {
        return yDataStore.read(dst);
    }

    synchronized int readZ(ByteBuffer dst) {
        return zDataStore.read(dst);
    }

    void putData(MotionData data) {
        xDataStore.putData(data.x);
        yDataStore.putData(data.y);
        zDataStore.putData(data.z);
    }

}
