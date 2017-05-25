package com.swm.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.sdk.HeartActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

class EngineeringAdapter extends BaseAdapter {
    private List<EngineeringModel> mEngineeringModels;

    EngineeringAdapter() {
        mEngineeringModels = new ArrayList<>();
        mEngineeringModels.add(new EngineeringModel("ECG", HeartActivity.class));
        mEngineeringModels.add(new EngineeringModel("Motion", MotionActivity.class));
    }

    @Override
    public int getCount() {
        return mEngineeringModels.size();
    }

    @Override
    public Object getItem(int position) {
        return mEngineeringModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEngineeringModels.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.swm_engineering_item, null, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.swm_engineering_title);
        title.setText(mEngineeringModels.get(position).title);
        convertView.setTag(mEngineeringModels.get(position).target);
        return convertView;
    }
}
