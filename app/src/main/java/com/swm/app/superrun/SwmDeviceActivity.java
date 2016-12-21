package com.swm.app.superrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.swm.core.TurnOnSwmDeviceGuide;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;

public class SwmDeviceActivity extends SwmBaseActivity implements View.OnClickListener {
    private AlertDialog mWarningDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swm_device);
        findViewById(R.id.swm_device_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.swm_device_edit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.swm_warning)
                    .setMessage(R.string.swm_remove_device)
                    .setPositiveButton(R.string.swm_cta_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SwmDeviceActivity.this, TurnOnSwmDeviceGuide.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.swm_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mWarningDialog.cancel();
                        }
                    });

            mWarningDialog = builder.create();
            mWarningDialog.show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWarningDialog != null && mWarningDialog.isShowing())
            mWarningDialog.cancel();
    }
}
