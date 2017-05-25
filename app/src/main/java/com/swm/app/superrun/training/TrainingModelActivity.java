package com.swm.app.superrun.training;

import android.os.Bundle;
import android.widget.ListView;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.training.TrainingModel;
import com.swm.training.TrainingModule;

import java.util.List;

public class TrainingModelActivity extends SwmBaseActivity {
    private TrainingAdapter mTrainingAdapter;

    static final String KEY_TRAINING_MODEL = "key_training_model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_course);
        ListView trainingList = (ListView) findViewById(R.id.swm_training_list);
        mTrainingAdapter = new TrainingAdapter();
        trainingList.setAdapter(mTrainingAdapter);
        List<TrainingModel> models = TrainingModule.getIns().getAllModel();
        mTrainingAdapter.setCourseList(models);

    }
}
