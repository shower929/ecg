package com.swm.app.superrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;

public class SettingsActivity extends SwmBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.swm_device).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.swm_device) {
            Intent intent = new Intent(this, SwmDeviceActivity.class);
            startActivity(intent);
        }
    }
}
