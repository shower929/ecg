package com.swm.ble;

/**
 * Created by yangzhenyu on 2017/3/20.
 */

public class PressurePacketLossModel extends PacketLossModel {
    @Override
    int indexColumn() {
        return 5;
    }
}
