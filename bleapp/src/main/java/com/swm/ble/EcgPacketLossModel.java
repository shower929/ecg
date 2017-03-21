package com.swm.ble;

/**
 * Created by yangzhenyu on 2017/3/16.
 */

public class EcgPacketLossModel extends PacketLossModel {

    @Override
    int indexColumn() {
        return 6;
    }


}
