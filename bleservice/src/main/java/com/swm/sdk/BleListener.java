package com.swm.sdk;

import java.util.UUID;

/**
 * Created by yangzhenyu on 2017/3/13.
 */

public interface BleListener {

    void onRawDataAvailable(UUID uuid, byte[] data);
}
