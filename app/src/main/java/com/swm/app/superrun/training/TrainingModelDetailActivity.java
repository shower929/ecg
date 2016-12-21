package com.swm.app.superrun.training;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.training.TrainingModel;

import static com.swm.app.superrun.training.TrainingModelActivity.KEY_TRAINING_MODEL;

public class TrainingModelDetailActivity extends SwmBaseActivity implements View.OnClickListener{
    private ExpViewController mExpViewController;
    private TrainingModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_detail);
        TextView titleView = (TextView) findViewById(R.id.swm_training_title);
        TextView trainingDesc = (TextView) findViewById(R.id.swm_training_description);
        Button cta = (Button)findViewById(R.id.swm_cta);
        TextView subAction = (TextView) findViewById(R.id.swm_sub_action);

        Intent intent = getIntent();
        mModel = intent.getExtras().getParcelable(KEY_TRAINING_MODEL);

        titleView.setText(mModel.title);
        trainingDesc.setText(mModel.description);

        if (mModel.distance > 0) {
            TextView distanceView = (TextView) findViewById(R.id.swm_training_distance);
            distanceView.setText(String.valueOf(mModel.distance));
            findViewById(R.id.swm_distance_layout).setVisibility(View.VISIBLE);
        }

        if(mModel.duration > 0) {
            TextView durationView = (TextView) findViewById(R.id.swm_training_duration);
            durationView.setText(String.valueOf(mModel.duration));
            findViewById(R.id.swm_duration).setVisibility(View.VISIBLE);
        }

        cta.setOnClickListener(this);
        subAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_cta:
                Intent intent = new Intent(this, TrainingActivity.class);
                intent.putExtra(KEY_TRAINING_MODEL, mModel);
                startActivity(intent);
                break;
            case R.id.swm_sub_action:
                break;
        }
    }
}
