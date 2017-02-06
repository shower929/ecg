package com.swm.app.superrun;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heart.pref.SwmPref;

public class AppInfoActivity extends SwmBaseActivity {
    TextView mFirmware;
    TextView mManufactureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        mFirmware = (TextView) findViewById(R.id.swm_fw_version);
        SharedPreferences preferences = getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        String firmware = preferences.getString(SwmPref.SWM_FIRMWARE, null);
        if(firmware != null)
            mFirmware.setText(firmware);

        mManufactureName = (TextView)findViewById(R.id.swm_manufacture_name);
        mManufactureName.setText(preferences.getString(SwmPref.SWM_MANUFACTURE_NAME, null));

    }
}
