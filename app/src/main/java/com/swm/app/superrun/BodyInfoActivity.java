package com.swm.app.superrun;

import android.os.Bundle;
import android.view.View;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;

public class BodyInfoActivity extends SwmBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_info);
        findViewById(R.id.swm_height_feet).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
