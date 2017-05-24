package com.swm.engineering;

import android.app.Activity;
import android.view.View;

import com.swm.engineering.app.R;
import com.swm.stuff.view.PacketView;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class DevicePresenter extends Presenter {
    private final Activity myActivity;
    private final PacketView packetView;

    public DevicePresenter(Activity activity, View view) {
        myActivity = activity;
        packetView = (PacketView) view.findViewById(R.id.swm_packet_view);

    }


    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void show() {
        onStart();
    }

    @Override
    public void hide() {
        onStop();
    }
}
