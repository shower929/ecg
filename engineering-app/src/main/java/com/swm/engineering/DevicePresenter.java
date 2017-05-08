package com.swm.engineering;

import android.app.Activity;
import android.view.View;

import com.swm.chart.Oscilloscope;
import com.swm.engineering.app.R;
import com.swm.sdk.BleDataSource;
import com.swm.stuff.view.PacketView;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class DevicePresenter extends Presenter {
    private final Activity myActivity;
    private final PacketView packetView;
    private BleDataSource bleDataSource;
    private final Oscilloscope throughputView;

    public DevicePresenter(Activity activity, View view) {
        myActivity = activity;
        packetView = (PacketView) view.findViewById(R.id.swm_packet_view);
        throughputView = (Oscilloscope) view.findViewById(R.id.swm_throughput_view);
    }


    @Override
    void onStart() {
        bleDataSource = new BleDataSource();
        throughputView.addDataSource(bleDataSource, myActivity.getResources().getColor(R.color.swm_white));

    }

    @Override
    void onStop() {
        throughputView.removeDataSource(bleDataSource);
    }

    @Override
    void show() {
        onStart();
    }

    @Override
    void hide() {
        onStop();
    }
}
