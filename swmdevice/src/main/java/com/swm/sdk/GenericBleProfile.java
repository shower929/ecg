package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/17.
 */

abstract class GenericBleProfile {
    abstract byte[] getEnableData();

    boolean enable(byte[] value) {
        byte[] enableData = getEnableData();
        if (enableData.length != value.length) {
            StringBuilder data = new StringBuilder();

            for(byte v: value)
                data.append(v);

            throw new RuntimeException("Invalid value: " + data.toString());
        }


        for(int i = 0; i < value.length; i++) {
            if (value[i] != enableData[i])
                return false;
        }

        return true;
    }
}
