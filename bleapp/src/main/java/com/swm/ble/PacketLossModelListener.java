package com.swm.ble;

/**
 * Created by yangzhenyu on 2017/3/16.
 */

public interface PacketLossModelListener {

    void onArrival(int index);
    void onPacketLoss(int loss);

}
