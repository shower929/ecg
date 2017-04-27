package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/27.
 */

class RawEcg extends DumpData {
    final byte[] raw;

    RawEcg(byte[] raw) {
        this.raw = raw;
    }

    @Override
    byte[] dump() {
        return raw;
    }
}
