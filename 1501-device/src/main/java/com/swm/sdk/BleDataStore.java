package com.swm.sdk;

import android.os.AsyncTask;
import android.os.Handler;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

class BleDataStore extends SwmDataStore{
    private static BleDataStore BLE_DATA_STORE;

    synchronized static BleDataStore getIns() {
        if (BLE_DATA_STORE == null)
            BLE_DATA_STORE = new BleDataStore();

        return BLE_DATA_STORE;
    }

}
