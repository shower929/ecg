package com.swm.app.superrun.training;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.training.TrainingModel;

import java.util.List;

import static com.swm.app.superrun.training.TrainingModelActivity.KEY_TRAINING_MODEL;

/**
 * Created by yangzhenyu on 2016/10/13.
 */

public class TrainingAdapter extends BaseAdapter implements View.OnClickListener{
    private List<TrainingModel> mCourseList;

    TrainingAdapter() {

    }

    @Override
    public int getCount() {
        return mCourseList == null ? 0 : mCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCourseList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.swm_training_item, null, false);
        }

        TrainingModel model = mCourseList.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.swm_training_title);
        title.setText(model.title);

        TextView desc = (TextView) convertView.findViewById(R.id.swm_training_brief);
        desc.setText(model.brief);

        Button cta = (Button) convertView.findViewById(R.id.swm_cta);
        cta.setTag(model);
        cta.setOnClickListener(this);

        return convertView;
    }

    void setCourseList(List courseList) {
        this.mCourseList = courseList;
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), TrainingModelDetailActivity.class);
        TrainingModel model = (TrainingModel) v.getTag();
        intent.putExtra(KEY_TRAINING_MODEL, model);
        v.getContext().startActivity(intent);
    }
}
