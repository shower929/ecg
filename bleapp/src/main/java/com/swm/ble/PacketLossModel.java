package com.swm.ble;

/**
 * Created by yangzhenyu on 2017/3/16.
 */

public abstract class PacketLossModel {
    PacketLossModelListener listener;
    private int preIndex;
    private int packetLoss;
    private volatile boolean init;
    private int packetCount;

    void setListener(PacketLossModelListener listener) {
        this.listener = listener;
    }

    public void offer(byte[] data) {
        int col = indexColumn();
        int index = data[col];

        if (listener != null)
            listener.onArrival(packetCount++);

        if (!init) {
            init = true;
            preIndex = index;
            return;
        }

        if(Math.abs(index - preIndex) % 255 > 1) {
            packetLoss = packetLoss + Math.abs(index - preIndex) % 255;
        }

        if (listener != null)
            listener.onPacketLoss(packetLoss);

        preIndex = index;
    }

    public void reset() {
        init = false;
        preIndex = 0;
        packetLoss = 0;
        packetCount = 0;
    }

    abstract int indexColumn();
}
